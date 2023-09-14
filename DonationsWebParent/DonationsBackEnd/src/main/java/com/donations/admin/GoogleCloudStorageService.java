package com.donations.admin;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Acl;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.Storage.BlobListOption;

@Service
public class GoogleCloudStorageService {
	private static final Logger LOGGER = LoggerFactory.getLogger(GoogleCloudStorageService.class);
	private static final String GCS_BUCKET_NAME;
	static {
		GCS_BUCKET_NAME = System.getenv("GCS_BUCKET_NAME");
	}
	private static Storage storage;

	public GoogleCloudStorageService(Storage storage) {
		this.storage = storage;
	}

	public static List<String> listFolders(String folderName) {
		Bucket bucket = storage.get(GCS_BUCKET_NAME);
		Page<Blob> blobs = bucket.list(BlobListOption.prefix(folderName));

		List<String> listKeys = new ArrayList<>();

		for (Blob blob : blobs.iterateAll()) {
			String objectName = blob.getName();
			if (objectName.startsWith(folderName)) {
				listKeys.add(objectName);
			}
		}
		return listKeys;
	}

	@SuppressWarnings("deprecation")
	public static void uploadFile(String folderName, String fileName, InputStream inputStream) {
		BlobId blobId = BlobId.of(GCS_BUCKET_NAME, folderName + "/" + fileName);
		List<Acl> acls = new ArrayList<Acl>();
		acls.add(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setAcl(acls).build();
		try (inputStream) {
			Blob blob = storage.create(blobInfo, inputStream);
			LOGGER.info(blob.toString());
		} catch (IOException exception) {
			LOGGER.error("Could not upload file to Amazon S3", exception);
		}
	}

	public static void deleteFile(String fileName) {
		BlobId blobId = BlobId.of(GCS_BUCKET_NAME, fileName);
		Blob blob = storage.get(blobId);
		if (blob != null && blob.exists()) {
			LOGGER.info("Blob exists.");
		} else {
			LOGGER.error("Blob does not exist.");
		}
		boolean deleted = storage.delete(blobId);
		if (deleted) {
			LOGGER.info(fileName + " is deleted successfully");
		} else {
			LOGGER.error("Something went wrong!");
		}
	}

	public static void removeFolder(String folderName) {
		Page<Blob> blobs = storage.list(GCS_BUCKET_NAME, BlobListOption.prefix(folderName));
		for (Blob blob : blobs.iterateAll()) {
			String objectName = blob.getName();
			boolean deleted = storage.delete(BlobId.of(GCS_BUCKET_NAME, objectName));
			if (deleted) {
				LOGGER.info("Deleted " + objectName);
			}
		}
	}
}

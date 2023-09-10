package com.donations.admin;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

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
	private final Storage storage;

	public GoogleCloudStorageService(Storage storage) {
		this.storage = storage;
	}

	public List<String> listFolders(String folderName) {
		String bucketName = System.getenv("GCS_BUCKET_NAME");
		Bucket bucket = storage.get(bucketName);
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

	public void uploadFile(String folderName, String fileName, InputStream inputStream) {
		String bucketName = System.getenv("GCS_BUCKET_NAME");
		BlobId blobId = BlobId.of(bucketName, folderName + "/" + fileName);
		List<Acl> acls = new ArrayList<Acl>();
		acls.add(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
		BlobInfo blobInfo = BlobInfo.newBuilder(blobId).setAcl(acls).build();
		Blob blob = storage.create(blobInfo, inputStream);
	}
}

package com.donations.admin;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.api.gax.paging.Page;
import com.google.cloud.storage.Blob;
import com.google.cloud.storage.Bucket;
import com.google.cloud.storage.Storage;
import com.google.cloud.storage.StorageOptions;

public class GoogleCloudStorageUtil {
	private static final Logger LOGGER = LoggerFactory.getLogger(GoogleCloudStorageUtil.class);

	private static final String BUCKET_NAME = "donations-project-391803.appspot.com"; 

    public static List<String> listObjectsInFolder(String folderName) {
        List<String> objectNames = new ArrayList<>();
        Storage storage = StorageOptions.getDefaultInstance().getService();

        Bucket bucket = storage.get(BUCKET_NAME);
        Page<Blob> blobs = bucket.list(Storage.BlobListOption.prefix(folderName));

        for (Blob blob : blobs.iterateAll()) {
            // Lọc ra chỉ các đối tượng nằm trong thư mục con, không bao gồm thư mục cha
            if (!blob.getName().equals(folderName + "/")) {
                objectNames.add(blob.getName());
            }
        }

        return objectNames;
    }

    public static void main(String[] args) {
        String folderName = "user-photos/"; 
        List<String> objectsInFolder = listObjectsInFolder(folderName);

        System.out.println("Danh sách các đối tượng trong thư mục " + folderName + ":");
        for (String objectName : objectsInFolder) {
            System.out.println(objectName);
        }
    }
}

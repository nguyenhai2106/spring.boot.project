package com.donations.admin;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class GoogleCloudStorageUtilTest {

	@Autowired
	private GoogleCloudStorageService service;

	@Test
	public void testListFolder() {
		String bucketName = System.getenv("GCS_BUCKET_NAME");
		System.out.println(bucketName);
		String folderName = "product-images/1";
		List<String> listFolders = service.listFolders(folderName);
		for (String folder : listFolders) {
			System.out.println(folder);
		}
	}

	@Test
	public void testUploadFile() throws FileNotFoundException {
		String folderName = "test";
		String fileName = "laptop.png";
		String filePath = "E:\\test\\" + fileName;
		InputStream inputStream = new FileInputStream(filePath);
		service.uploadFile(folderName, fileName, inputStream);
	}

	@Test
	public void testDeleteFile() {
		String fileName = "test/laptop.png";
		service.deleteFile(fileName);
	}

	@Test
	public void testRemoveFolder() {
		String folderName = "test";
		service.removeFolder(folderName);
	}
}

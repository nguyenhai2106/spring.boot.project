package com.donations.admin;

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
		String folderName = "product-images/";
		List<String> listFolders = service.listFolders(folderName);
		for (String folder : listFolders) {
			System.out.println(folder);
		}
	}
}

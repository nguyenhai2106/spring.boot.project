package com.donations.admin.product;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.donations.admin.GoogleCloudStorageService;
import com.donations.common.entity.product.Product;
import com.donations.common.entity.product.ProductImage;

public class ProductSaveHelper {
	private static final Logger LOGGER = LoggerFactory.getLogger(ProductController.class);

	static void deleteExtraImageWeredRemoveOnForm(Product product) {
		String extraImageDir = "product-images/" + product.getId() + "/extras";
		List<String> listObjectKey = GoogleCloudStorageService.listFolders(extraImageDir);
		for (String objectKey : listObjectKey) {
			int lastIndexOfSlash = objectKey.lastIndexOf("/");
			String fileName = objectKey.substring(lastIndexOfSlash + 1, objectKey.length());
			if (!product.containsImageName(fileName)) {
				GoogleCloudStorageService.deleteFile(objectKey);
				System.out.println("Deleted: " + objectKey);
			}
		}
	}

	static void setExistingExtraImageNames(String[] imageIDs, String[] imageNames, Product product) {

		if (imageIDs == null || imageIDs.length == 0) {
			return;
		}

		Set<ProductImage> images = new HashSet<>();

		for (int i = 0; i < imageIDs.length; i++) {
			Integer id = Integer.parseInt(imageIDs[i]);
			String name = imageNames[i];
			images.add(new ProductImage(id, name, product));
		}

		product.setImages(images);
	}

	static void setProductDetails(Product product, String[] detailNames, String[] detailValues, String[] detailIDs) {
		if (detailNames == null || detailNames.length == 0) {
			return;
		}
		for (int i = 0; i < detailNames.length; i++) {
			String name = detailNames[i];
			String value = detailValues[i];
			Integer id = Integer.parseInt(detailIDs[i]);
			if (id != 0) {
				product.addDetail(id, name, value);
			} else if (!name.isEmpty() && !value.isEmpty()) {
				product.addDetail(name, value);
			}
		}
	}

	static void saveUploadedImages(MultipartFile mainImageMultipartFile, MultipartFile[] extraImageMultipartFile,
			Product savedProduct) throws IOException {
		if (!mainImageMultipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(mainImageMultipartFile.getOriginalFilename());
			String uploadDir = "product-images/" + savedProduct.getId();
			System.out.println(uploadDir);
			List<String> listObjectKey = GoogleCloudStorageService.listFolders(uploadDir);
			for (String objectKey : listObjectKey) {
				if (!objectKey.contains("/extras/")) {
					GoogleCloudStorageService.deleteFile(objectKey);
				}
			}
			GoogleCloudStorageService.uploadFile(uploadDir, fileName, mainImageMultipartFile.getInputStream());
		}
		if (extraImageMultipartFile.length > 0) {
			String uploadDir = "product-images/" + savedProduct.getId() + "/extras";
			for (MultipartFile multipartFile : extraImageMultipartFile) {
				if (multipartFile.isEmpty())
					continue;
				String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
				GoogleCloudStorageService.uploadFile(uploadDir, fileName, multipartFile.getInputStream());
			}
		}

	}

	static void setNewExtraImageNames(Product product, MultipartFile[] extraImageMultipartFile) {
		if (extraImageMultipartFile.length > 0) {
			for (MultipartFile multipartFile : extraImageMultipartFile) {
				if (!multipartFile.isEmpty()) {
					String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
					if (!product.containsImageName(fileName)) {
						product.addExtraImage(fileName);
					}
				}
			}
		}

	}

	static void setMainImageName(Product product, MultipartFile multipartFile) {
		if (!multipartFile.isEmpty()) {
			String fileName = StringUtils.cleanPath(multipartFile.getOriginalFilename());
			product.setMainImage(fileName);
		}
	}
}

package com.donations.common.constants;

public class Constants {
    public static final String GCS_BASE_URI;
    
    static {
        String bucketName = System.getenv("GCS_BUCKET_NAME");
        if (bucketName != null) {
            GCS_BASE_URI = "https://storage.cloud.google.com/" + bucketName;
        } else {
            GCS_BASE_URI = "";
        }
    }
}

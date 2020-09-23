package s3.upload;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class Files2S3 {
  public static void main(String[] args) {
    // required env vars:
    // AWS_PROFILE=sonarcloud-dev-admin
    // AWS_REGION=eu-central-1
    Params params = Params.parse(args);

    S3Client s3Client = S3Client.builder().build();

    if (params.paths.isEmpty()) {
      throw new IllegalArgumentException("No matching paths to upload");
    }

    params.paths.forEach(path -> {
      System.out.println("Uploading file: " + path);
      PutObjectRequest request = PutObjectRequest.builder()
        .bucket(params.bucketName)
        .key(path.getFileName().toString())
        .build();
      s3Client.putObject(
        request,
        RequestBody.fromFile(path));
    });
  }
}

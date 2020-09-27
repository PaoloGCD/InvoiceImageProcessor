package com.example.ramhacks_invoicescan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import org.opencv.android.Utils;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class ActivityPicturePreview extends AppCompatActivity {

    private static final String TAG = "ActivityPicturePreview";

    private String mImagePath;
    private String mImageName;
    private String mBlurring;
    private String mStdv;
    private int mImageHeight;
    private int mImageWidth;

    private String mDate;
    private String mPrice;
    private String mStore;
    private String mItems;

    private TextView mStdvView;
    private ImageView mImageView;
    private TextView mDateView;
    private TextView mPriceView;
    private TextView mItemsView;
    private TextView mStoreView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_preview);

        mStdvView = findViewById(R.id.textViewStdv);
        mImageView = (ImageView) findViewById(R.id.imageView);

        mDateView = findViewById(R.id.textViewDate);
        mPriceView = findViewById(R.id.textViewPrice);
        mItemsView = findViewById(R.id.textViewItems);
        mStoreView = findViewById(R.id.textViewStore);

        mStore = "Kroger";
        mItems = "16";

        // Store is still not processed
        mStoreView.setText(mStore);

        Intent intent = getIntent();
        mImagePath = intent.getStringExtra("imagePath");
        mImageName = intent.getStringExtra("imageName");
        mBlurring = intent.getStringExtra("imageBlurring");
        mStdv = intent.getStringExtra("imageStdv");
        mImageHeight = intent.getIntExtra("imageHeight", 0);
        mImageWidth = intent.getIntExtra("imageWidth", 0);

        // set image
        File imgFile = new File(mImagePath);
        Bitmap mCacheBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());

        // extract data
        InputImage image = InputImage.fromBitmap(mCacheBitmap, 0);
        TextRecognizer recognizer = TextRecognition.getClient();

        recognizer.process(image)
                .addOnSuccessListener(new OnSuccessListener<com.google.mlkit.vision.text.Text>() {
                    @Override
                    public void onSuccess(com.google.mlkit.vision.text.Text visionText) {

                        String patternDate = "../../..*";
                        String patternPrice = "REF.*TOTAL.*";

                        Mat matImage = Imgcodecs.imread(mImagePath);

                        String resultText = visionText.getText();

                        int cornerItemsBlock = 0;
                        ArrayList<Integer> cornerList = new ArrayList<>();

                        for (com.google.mlkit.vision.text.Text.TextBlock block : visionText.getTextBlocks()) {
                            String blockText = block.getText();

                            android.graphics.Point[] blockCornerPoints = block.getCornerPoints();
                            cornerList.add(blockCornerPoints[0].y);

                            if (blockText.toLowerCase().contains("number of items")){
                                cornerItemsBlock = cornerItemsBlock + blockCornerPoints[0].y;
//                                Imgproc.line(
//                                        matImage,
//                                        new Point(blockCornerPoints[0].x, blockCornerPoints[0].y),
//                                        new Point(blockCornerPoints[1].x, blockCornerPoints[1].y),
//                                        new Scalar(50, 50, 50), 2);
//                                Imgproc.line(
//                                        matImage,
//                                        new Point(blockCornerPoints[1].x, blockCornerPoints[1].y),
//                                        new Point(blockCornerPoints[2].x, blockCornerPoints[2].y),
//                                        new Scalar(50, 50, 50), 2);
//                                Imgproc.line(
//                                        matImage,
//                                        new Point(blockCornerPoints[2].x, blockCornerPoints[2].y),
//                                        new Point(blockCornerPoints[3].x, blockCornerPoints[3].y),
//                                        new Scalar(50, 50, 50), 2);
//
//                                Imgproc.line(
//                                        matImage,
//                                        new Point(blockCornerPoints[3].x, blockCornerPoints[3].y),
//                                        new Point(blockCornerPoints[0].x, blockCornerPoints[0].y),
//                                        new Scalar(50, 50, 50), 2);
                                continue;
                            }

//                            Imgproc.line(
//                                    matImage,
//                                    new Point(blockCornerPoints[0].x, blockCornerPoints[0].y),
//                                    new Point(blockCornerPoints[1].x, blockCornerPoints[1].y),
//                                    new Scalar(165, 165, 165), 2);
//
//                            Imgproc.line(
//                                    matImage,
//                                    new Point(blockCornerPoints[1].x, blockCornerPoints[1].y),
//                                    new Point(blockCornerPoints[2].x, blockCornerPoints[2].y),
//                                    new Scalar(165, 165, 165), 2);
//
//                            Imgproc.line(
//                                    matImage,
//                                    new Point(blockCornerPoints[2].x, blockCornerPoints[2].y),
//                                    new Point(blockCornerPoints[3].x, blockCornerPoints[3].y),
//                                    new Scalar(165, 165, 165), 2);
//
//                            Imgproc.line(
//                                    matImage,
//                                    new Point(blockCornerPoints[3].x, blockCornerPoints[3].y),
//                                    new Point(blockCornerPoints[0].x, blockCornerPoints[0].y),
//                                    new Scalar(165, 165, 165), 2);

                            Rect blockFrame = block.getBoundingBox();
                            for (com.google.mlkit.vision.text.Text.Line line : block.getLines()) {
                                String lineText = line.getText();
                                android.graphics.Point[] lineCornerPoints = line.getCornerPoints();

                                if (lineText.length() > 16){
                                    if (Pattern.matches(patternDate, lineText)){
                                        mDate = lineText.substring(0, 16);
                                        mDateView.setText(mDate);

                                        Imgproc.line(
                                                matImage,
                                                new Point(lineCornerPoints[0].x, lineCornerPoints[0].y),
                                                new Point(lineCornerPoints[1].x, lineCornerPoints[1].y),
                                                new Scalar(255, 0, 0), 2);
                                        Imgproc.line(
                                                matImage,
                                                new Point(lineCornerPoints[1].x, lineCornerPoints[1].y),
                                                new Point(lineCornerPoints[2].x, lineCornerPoints[2].y),
                                                new Scalar(255, 0, 0), 2);
                                        Imgproc.line(
                                                matImage,
                                                new Point(lineCornerPoints[2].x, lineCornerPoints[2].y),
                                                new Point(lineCornerPoints[3].x, lineCornerPoints[3].y),
                                                new Scalar(255, 0, 0), 2);

                                        Imgproc.line(
                                                matImage,
                                                new Point(lineCornerPoints[3].x, lineCornerPoints[3].y),
                                                new Point(lineCornerPoints[0].x, lineCornerPoints[0].y),
                                                new Scalar(255, 0, 0), 2);
                                        break;
                                    }
                                }

                                if (lineText.length() > 22){
                                    if (Pattern.matches(patternPrice, lineText)){
                                        mPrice = lineText.substring(20);
                                        mPriceView.setText(mPrice);

                                        Imgproc.line(
                                                matImage,
                                                new Point(lineCornerPoints[0].x, lineCornerPoints[0].y),
                                                new Point(lineCornerPoints[1].x, lineCornerPoints[1].y),
                                                new Scalar(255, 0, 0), 2);
                                        Imgproc.line(
                                                matImage,
                                                new Point(lineCornerPoints[1].x, lineCornerPoints[1].y),
                                                new Point(lineCornerPoints[2].x, lineCornerPoints[2].y),
                                                new Scalar(255, 0, 0), 2);
                                        Imgproc.line(
                                                matImage,
                                                new Point(lineCornerPoints[2].x, lineCornerPoints[2].y),
                                                new Point(lineCornerPoints[3].x, lineCornerPoints[3].y),
                                                new Scalar(255, 0, 0), 2);

                                        Imgproc.line(
                                                matImage,
                                                new Point(lineCornerPoints[3].x, lineCornerPoints[3].y),
                                                new Point(lineCornerPoints[0].x, lineCornerPoints[0].y),
                                                new Scalar(255, 0, 0), 2);
                                        break;
                                    }
                                }

                                Rect lineFrame = line.getBoundingBox();
                                for (Text.Element element : line.getElements()) {
                                    String elementText = element.getText();
                                    android.graphics.Point[] elementCornerPoints = element.getCornerPoints();
                                    Rect elementFrame = element.getBoundingBox();
                                }
                            }
                        }


                        int difference;
                        int best_block = 0;
                        int best_difference = 10000;
                        for (int i = 0; i < cornerList.size(); i++) {
                            difference = Math.abs(cornerItemsBlock - cornerList.get(i));
                            if (difference < best_difference && difference != 0){
                                best_difference = difference;
                                best_block = i;
                            }
                        }

                        int i = 0;
                        for (com.google.mlkit.vision.text.Text.TextBlock block : visionText.getTextBlocks()){
                            if (i == best_block){
                                mItems = block.getText();
                                mItemsView.setText(mItems);
                                android.graphics.Point[] blockCornerPoints = block.getCornerPoints();

                                Imgproc.line(
                                        matImage,
                                        new Point(blockCornerPoints[0].x, blockCornerPoints[0].y),
                                        new Point(blockCornerPoints[1].x, blockCornerPoints[1].y),
                                        new Scalar(255, 0, 0), 2);
                                Imgproc.line(
                                        matImage,
                                        new Point(blockCornerPoints[1].x, blockCornerPoints[1].y),
                                        new Point(blockCornerPoints[2].x, blockCornerPoints[2].y),
                                        new Scalar(255, 0, 0), 2);
                                Imgproc.line(
                                        matImage,
                                        new Point(blockCornerPoints[2].x, blockCornerPoints[2].y),
                                        new Point(blockCornerPoints[3].x, blockCornerPoints[3].y),
                                        new Scalar(255, 0, 0), 2);

                                Imgproc.line(
                                        matImage,
                                        new Point(blockCornerPoints[3].x, blockCornerPoints[3].y),
                                        new Point(blockCornerPoints[0].x, blockCornerPoints[0].y),
                                        new Scalar(255, 0, 0), 2);
                                break;
                            }
                            i = i + 1;
                        }

                        Imgcodecs.imwrite(mImagePath, matImage);

                        Bitmap mBitmap = Bitmap.createBitmap(matImage.cols(), matImage.rows(), Bitmap.Config.ARGB_8888);
                        Utils.matToBitmap(matImage, mBitmap);

                        mImageView.setImageBitmap(mBitmap);
                    }
                })
                .addOnFailureListener( new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                    }
                });

        // set text data
        mStdvView.setText(mStdv);
    }

    public void acceptPicture(View v){

        // Save image in FireBaseDB
        Log.i(TAG, "Saving in Firebase");
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageReference = storage.getReference();

        Uri file = Uri.fromFile(new File(mImagePath));
        StorageReference ref = storageReference.child("documents/" + mImageName);

        ref.putFile(file)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(ActivityPicturePreview.this, "Uploaded", Toast.LENGTH_LONG).show();
                        Log.i(TAG, "Image uploaded on firebase");

                        Intent result = new Intent();
                        result.putExtra("imageDate", mDate);
                        result.putExtra("imagePrice", mPrice);
                        result.putExtra("imageStore", mStore);
                        result.putExtra("imageItems", mItems);
                        setResult(RESULT_OK, result);
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(ActivityPicturePreview.this, "Failed "+e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    }
                });
    }

    public void rejectPicture(View v){
        Intent result = new Intent();
        setResult(RESULT_CANCELED, result);
        finish();
    }

}
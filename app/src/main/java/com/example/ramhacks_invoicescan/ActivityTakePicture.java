package com.example.ramhacks_invoicescan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.mlkit.vision.common.InputImage;
import com.google.mlkit.vision.label.ImageLabel;
import com.google.mlkit.vision.label.ImageLabeler;
import com.google.mlkit.vision.label.ImageLabeling;
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions;
import com.google.mlkit.vision.text.Text;
import com.google.mlkit.vision.text.TextRecognition;
import com.google.mlkit.vision.text.TextRecognizer;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfDouble;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

public class ActivityTakePicture extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, Camera.PictureCallback{

    private static final String TAG = "ActivityTakePicture";

    static final int PICTURE_PREVIEW_REQUEST = 21;

    private JavaCameraView mOpenCvCameraView;
    private FloatingActionButton mFloatingButton;
    private TextView mTextViewLabel;

    private String mImageName;
    private String mImagePath;
    private String mImageType;

    private int mFrameWidth;
    private int mFrameHeight;

    static {
        if (!OpenCVLoader.initDebug()) {
            Log.e("OpenCV", "error while opening openCV");
        } else {
            Log.e("OpenCV", "successful openCV initiation");
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_take_picture);

        mTextViewLabel = findViewById(R.id.textViewLabel);

        mOpenCvCameraView = findViewById(R.id.OpenCvView);
        mOpenCvCameraView.setVisibility(CameraBridgeViewBase.VISIBLE);
        mOpenCvCameraView.setFrameRotation(90);
        mOpenCvCameraView.setCvCameraViewListener(this);

        mOpenCvCameraView.enableView();

        mFloatingButton = findViewById(R.id.fab);

    }

    @Override
    public void onCameraViewStarted(int width, int height) {

        Camera.Size pictureSize = mOpenCvCameraView.getCamera().getParameters().getPictureSize();
        mFrameWidth = pictureSize.width;
        mFrameHeight = pictureSize.height;

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat miRGB = inputFrame.rgba();

        Mat miYUV = inputFrame.yuv420sp();

        Bitmap mCacheBitmap = Bitmap.createBitmap(miYUV.cols(), miYUV.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(miYUV, mCacheBitmap);

        InputImage image = InputImage.fromBitmap(mCacheBitmap, 90);

        ImageLabeler labeler = ImageLabeling.getClient(ImageLabelerOptions.DEFAULT_OPTIONS);
        labeler.process(image)
                .addOnSuccessListener(new OnSuccessListener<List<ImageLabel>>() {
                    @Override
                    public void onSuccess(List<ImageLabel> labels) {
                        mTextViewLabel.setText(labels.get(0).getText());
                        mImageType = labels.get(0).getText();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        // Task failed with an exception
                        // ...
                    }
                });

        return miRGB;
    }

    public void takePicture(View v){
        mFloatingButton.setClickable(false);

        mOpenCvCameraView.getCamera().setPreviewCallback(null);
        mOpenCvCameraView.getCamera().takePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.i(TAG, "On picture taken called");

        // Start camera preview
        mOpenCvCameraView.getCamera().startPreview();
        mOpenCvCameraView.getCamera().setPreviewCallback(mOpenCvCameraView);

        // Get size
        Camera.Size pictureSize = mOpenCvCameraView.getCamera().getParameters().getPictureSize();

        // Get RGB mat image
        Mat rgbMat = Imgcodecs.imdecode(new MatOfByte(data), Imgcodecs.CV_LOAD_IMAGE_UNCHANGED);
        Core.transpose(rgbMat, rgbMat);
        Core.flip(rgbMat, rgbMat, 1);

        // Get blurring
        Mat laplacianResult = new Mat();
        MatOfDouble mean = new MatOfDouble();
        MatOfDouble dev = new MatOfDouble();

        Imgproc.Laplacian(rgbMat, laplacianResult, CvType.CV_16S);

        Core.convertScaleAbs(laplacianResult, laplacianResult);
        Core.meanStdDev(laplacianResult, mean, dev);

        double[] meanDouble = mean.toArray();
        double[] stdvDouble = dev.toArray();

        String blurringString = String.format(Locale.getDefault(),"[%.2f, %.2f, %.2f]", meanDouble[0], meanDouble[1], meanDouble[2]);
        String stdvString = String.format(Locale.getDefault(),"[%.2f, %.2f, %.2f]", stdvDouble[0], stdvDouble[1], stdvDouble[2]);

        // Save image
        String destPath = Objects.requireNonNull(getExternalFilesDir(null)).getAbsolutePath();
        String timeStamp =  new SimpleDateFormat("yyyyMMdd_HHmmssSSS", Locale.getDefault()).format(new Date());
        mImageName = "IMG_" + timeStamp + ".jpg";
        mImagePath = destPath + File.separator + mImageName;

        Imgcodecs.imwrite(mImagePath, rgbMat);
        Log.i(TAG, "Image saved in memory");

        // Go to picture preview
        Intent intent = new Intent(this, ActivityPicturePreview.class);
        intent.putExtra("imagePath", mImagePath);
        intent.putExtra("imageName", mImageName);
        intent.putExtra("imageBlurring", blurringString);
        intent.putExtra("imageStdv", stdvString);
        intent.putExtra("imageHeight", pictureSize.width);
        intent.putExtra("imageWidth", pictureSize.height);

        startActivityForResult(intent, PICTURE_PREVIEW_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent result){
        super.onActivityResult(requestCode, resultCode, result);

        if(requestCode == PICTURE_PREVIEW_REQUEST){
            if(resultCode == Activity.RESULT_OK){

                InvoiceElement invoice = new InvoiceElement();
                invoice.mPath = mImagePath;
                invoice.mType = mImageType;

                invoice.mStore = result.getStringExtra("imageStore");
                invoice.mCost = result.getStringExtra("imagePrice");
                invoice.mDate = result.getStringExtra("imageDate");
                invoice.mItems = result.getStringExtra("imageItems");

                // Save baby in FireBaseDb
                DatabaseReference mFireBaseDB;
                mFireBaseDB = FirebaseDatabase.getInstance().getReference();
                DatabaseReference NewInvoiceReference = mFireBaseDB.child("InvoiceTable").push();

                invoice.mWebId = NewInvoiceReference.getKey();

                InvoiceDbHandler dataBase = new InvoiceDbHandler(this);
                invoice.mId = dataBase.addInvoice(invoice);

                NewInvoiceReference.setValue(invoice);

                Intent res = new Intent();
                res.putExtra("imageId", invoice.mId);
                res.putExtra("imagePath", invoice.mPath);
                res.putExtra("imageType", invoice.mType);
                res.putExtra("imageDate", invoice.mStore);
                res.putExtra("imagePrice", invoice.mCost);
                res.putExtra("imageStore", invoice.mDate);
                res.putExtra("imageItems", invoice.mItems);
                setResult(RESULT_OK, result);

                finish();
            } else {
                Toast.makeText(ActivityTakePicture.this, "Please, take a new picture", Toast.LENGTH_SHORT).show();
            }
        }

        mFloatingButton.setClickable(true);
    }
}
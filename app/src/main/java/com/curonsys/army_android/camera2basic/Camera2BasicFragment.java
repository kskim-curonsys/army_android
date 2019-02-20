/*
 * Copyright 2017 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.curonsys.army_android.camera2basic;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.RectF;
import android.graphics.SurfaceTexture;
import android.hardware.camera2.CameraAccessException;
import android.hardware.camera2.CameraCaptureSession;
import android.hardware.camera2.CameraCharacteristics;
import android.hardware.camera2.CameraDevice;
import android.hardware.camera2.CameraManager;
import android.hardware.camera2.CameraMetadata;
import android.hardware.camera2.CaptureRequest;
import android.hardware.camera2.CaptureResult;
import android.hardware.camera2.TotalCaptureResult;
import android.hardware.camera2.params.StreamConfigurationMap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.media.Image;
import android.media.ImageReader;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
import android.util.Log;
import android.util.Size;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.Surface;
import android.view.TextureView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.afollestad.materialdialogs.MaterialDialog;
import com.airbnb.lottie.LottieAnimationView;
import com.curonsys.army_android.util.CallBackListener;
import com.curonsys.army_android.R;
import com.curonsys.army_android.util.RequestManager;
import com.curonsys.army_android.model.BusinessCardModel;
import com.curonsys.army_android.model.ContentModel;
import com.curonsys.army_android.model.MarkerModel;
import com.curonsys.army_android.model.TransferModel;
import com.curonsys.army_android.util.SharedDataManager;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

import static android.content.ContentValues.TAG;
import static android.widget.Toast.LENGTH_SHORT;

public class Camera2BasicFragment extends Fragment
        implements View.OnClickListener, ActivityCompat.OnRequestPermissionsResultCallback {
    /**
     * Conversion from screen rotation to JPEG orientation.
     */
    private static final SparseIntArray ORIENTATIONS = new SparseIntArray();
    private static final int REQUEST_CAMERA_PERMISSION = 1;
    private static final String FRAGMENT_DIALOG = "dialog";
    private LocationManager lm;
    private boolean swich = false;
    MaterialDialog.Builder builder = null;
    MaterialDialog materialDialog = null;
    CallBackListener callBackListener;
    private static File capture_path;
    private static String marker_url = "";
    private static String marker_id = "";
    private static String card_id = "";
    private String phone_num ="";

    private Activity mContext;
    ContentModel contentModel;
    ArrayList<String> textures = new ArrayList<String>();
    String modelUrl;
    public int textureCount =0;
    ContentModel contentModel_putExtra;
    boolean hadMarker = false;
    LottieAnimationView lottie;

    static {
        ORIENTATIONS.append(Surface.ROTATION_0, 90);
        ORIENTATIONS.append(Surface.ROTATION_90, 0);
        ORIENTATIONS.append(Surface.ROTATION_180, 270);
        ORIENTATIONS.append(Surface.ROTATION_270, 180);
    }

    /**
     * Tag for the {@link Log}.
     */
    private static final String TAG = "Camera2BasicFragment";

    /**
     * Camera state: Showing camera preview.
     */
    private static final int STATE_PREVIEW = 0;

    /**
     * Camera state: Waiting for the focus to be locked.
     */
    private static final int STATE_WAITING_LOCK = 1;

    /**
     * Camera state: Waiting for the exposure to be precapture state.
     */
    private static final int STATE_WAITING_PRECAPTURE = 2;

    /**
     * Camera state: Waiting for the exposure state to be something other than precapture.
     */
    private static final int STATE_WAITING_NON_PRECAPTURE = 3;

    /**
     * Camera state: Picture was taken.
     */
    private static final int STATE_PICTURE_TAKEN = 4;

    /**
     * Max preview width that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_WIDTH = 2560;
    //1920
    /**
     * Max preview height that is guaranteed by Camera2 API
     */
    private static final int MAX_PREVIEW_HEIGHT = 1440;
    //1080
    private Context materialView;

    /**
     * {@link TextureView.SurfaceTextureListener} handles several lifecycle events on a
     * {@link TextureView}.
     */

    @Override
    public void onAttach(Activity activity) {
        // TODO Auto-generated method stub
        super.onAttach(activity);
        mContext=activity;
        //step 4

    }

    private final TextureView.SurfaceTextureListener mSurfaceTextureListener
            = new TextureView.SurfaceTextureListener() {

        @Override
        public void onSurfaceTextureAvailable(SurfaceTexture texture, int width, int height) {
            openCamera(width/4, height/4);
        }

        @Override
        public void onSurfaceTextureSizeChanged(SurfaceTexture texture, int width, int height) {
            configureTransform(width/4, height/4);
        }

        @Override
        public boolean onSurfaceTextureDestroyed(SurfaceTexture texture) {
            return true;
        }

        @Override
        public void onSurfaceTextureUpdated(SurfaceTexture texture) {
        }

    };

    /**
     * ID of the current {@link CameraDevice}.
     */
    private String mCameraId;

    /**
     * An {@link AutoFitTextureView} for camera preview.
     */
    private AutoFitTextureView mTextureView;

    /**
     * A {@link CameraCaptureSession } for camera preview.
     */
    private CameraCaptureSession mCaptureSession;

    /**
     * A reference to the opened {@link CameraDevice}.
     */
    private CameraDevice mCameraDevice;

    /**
     * The {@link Size} of camera preview.
     */
    private Size mPreviewSize;

    /**
     * {@link CameraDevice.StateCallback} is called when {@link CameraDevice} changes its state.
     */

    final Handler handler = new Handler()
    {
        public void handleMessage(Message msg)
        {
            //UI 변경 작업을 코딩하세요.
            lottie.cancelAnimation();
            lottie.setVisibility(View.GONE);
        }
    };


    private final CameraDevice.StateCallback mStateCallback = new CameraDevice.StateCallback() {

        @Override
        public void onOpened(@NonNull CameraDevice cameraDevice) {
            // This method is called when the camera is opened.  We start camera preview here.
            mCameraOpenCloseLock.release();
            mCameraDevice = cameraDevice;
            createCameraPreviewSession();
        }

        @Override
        public void onDisconnected(@NonNull CameraDevice cameraDevice) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
        }

        @Override
        public void onError(@NonNull CameraDevice cameraDevice, int error) {
            mCameraOpenCloseLock.release();
            cameraDevice.close();
            mCameraDevice = null;
            Activity activity = getActivity();
            if (null != activity) {
                activity.finish();
            }
        }

    };

    /**
     * An additional thread for running tasks that shouldn't block the UI.
     */
    private HandlerThread mBackgroundThread;

    /**
     * A {@link Handler} for running tasks in the background.
     */
    private Handler mBackgroundHandler;

    /**
     * An {@link ImageReader} that handles still image capture.
     */
    private ImageReader mImageReader;

    /**
     * This is the output file for our picture.
     */
    private File mFile;

    /**
     * This a callback object for the {@link ImageReader}. "onImageAvailable" will be called when a
     * still image is ready to be saved.
     */
    private final ImageReader.OnImageAvailableListener mOnImageAvailableListener
            = new ImageReader.OnImageAvailableListener() {

        @Override
        public void onImageAvailable(ImageReader reader) {
            uploadData(getActivity());
            mBackgroundHandler.post(new ImageSaver(reader.acquireNextImage(), mFile, getActivity()));
        }


    };

    /**
     * {@link CaptureRequest.Builder} for the camera preview
     */
    private CaptureRequest.Builder mPreviewRequestBuilder;

    /**
     * {@link CaptureRequest} generated by {@link #mPreviewRequestBuilder}
     */
    private CaptureRequest mPreviewRequest;

    /**
     * The current state of camera state for taking pictures.
     *
     * @see #mCaptureCallback
     */
    private int mState = STATE_PREVIEW;

    /**
     * A {@link Semaphore} to prevent the app from exiting before closing the camera.
     */
    private Semaphore mCameraOpenCloseLock = new Semaphore(1);

    /**
     * Whether the current camera device supports Flash or not.
     */
    private boolean mFlashSupported;

    /**
     * Orientation of the camera sensor
     */
    private int mSensorOrientation;

    /**
     * A {@link CameraCaptureSession.CaptureCallback} that handles events related to JPEG capture.
     */
    private CameraCaptureSession.CaptureCallback mCaptureCallback
            = new CameraCaptureSession.CaptureCallback() {

        private void process(CaptureResult result) {
            switch (mState) {
                case STATE_PREVIEW: {
                    // We have nothing to do when the camera preview is working normally.
                    break;
                }
                case STATE_WAITING_LOCK: {
                    Integer afState = result.get(CaptureResult.CONTROL_AF_STATE);
                    if (afState == null) {
                        captureStillPicture();
                    } else if (CaptureResult.CONTROL_AF_STATE_FOCUSED_LOCKED == afState ||
                            CaptureResult.CONTROL_AF_STATE_NOT_FOCUSED_LOCKED == afState) {
                        // CONTROL_AE_STATE can be null on some devices
                        Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                        if (aeState == null ||
                                aeState == CaptureResult.CONTROL_AE_STATE_CONVERGED) {
                            mState = STATE_PICTURE_TAKEN;
                            captureStillPicture();
                        } else {
                            runPrecaptureSequence();
                        }
                    }
                    break;
                }
                case STATE_WAITING_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null ||
                            aeState == CaptureResult.CONTROL_AE_STATE_PRECAPTURE ||
                            aeState == CaptureRequest.CONTROL_AE_STATE_FLASH_REQUIRED) {
                        mState = STATE_WAITING_NON_PRECAPTURE;
                    }
                    break;
                }
                case STATE_WAITING_NON_PRECAPTURE: {
                    // CONTROL_AE_STATE can be null on some devices
                    Integer aeState = result.get(CaptureResult.CONTROL_AE_STATE);
                    if (aeState == null || aeState != CaptureResult.CONTROL_AE_STATE_PRECAPTURE) {
                        mState = STATE_PICTURE_TAKEN;
                        captureStillPicture();
                    }
                    break;
                }
            }
        }

        @Override
        public void onCaptureProgressed(@NonNull CameraCaptureSession session,
                                        @NonNull CaptureRequest request,
                                        @NonNull CaptureResult partialResult) {
            process(partialResult);
        }

        @Override
        public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                       @NonNull CaptureRequest request,
                                       @NonNull TotalCaptureResult result) {
            process(result);
        }

    };

    /**
     * Shows a {@link Toast} on the UI thread.
     *
     * @param text The message to show
     */
    private void showToast(final String text) {
        final Activity activity = getActivity();
        if (activity != null) {
            activity.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(activity, text, LENGTH_SHORT).show();
                }
            });
        }
    }

    /**
     * Given {@code choices} of {@code Size}s supported by a camera, choose the smallest one that
     * is at least as large as the respective texture view size, and that is at most as large as the
     * respective max size, and whose aspect ratio matches with the specified value. If such size
     * doesn't exist, choose the largest one that is at most as large as the respective max size,
     * and whose aspect ratio matches with the specified value.
     *
     * @param choices           The list of sizes that the camera supports for the intended output
     *                          class
     * @param textureViewWidth  The width of the texture view relative to sensor coordinate
     * @param textureViewHeight The height of the texture view relative to sensor coordinate
     * @param maxWidth          The maximum width that can be chosen
     * @param maxHeight         The maximum height that can be chosen
     * @param aspectRatio       The aspect ratio
     * @return The optimal {@code Size}, or an arbitrary one if none were big enough
     */
    private static Size chooseOptimalSize(Size[] choices, int textureViewWidth,
            int textureViewHeight, int maxWidth, int maxHeight, Size aspectRatio) {

        // Collect the supported resolutions that are at least as big as the preview Surface
        List<Size> bigEnough = new ArrayList<>();
        // Collect the supported resolutions that are smaller than the preview Surface
        List<Size> notBigEnough = new ArrayList<>();
        int w = aspectRatio.getWidth();
        int h = aspectRatio.getHeight();
        for (Size option : choices) {
            if (option.getWidth() <= maxWidth && option.getHeight() <= maxHeight &&
                    option.getHeight() == option.getWidth() * h / w) {
                if (option.getWidth() >= textureViewWidth &&
                    option.getHeight() >= textureViewHeight) {
                    bigEnough.add(option);
                } else {
                    notBigEnough.add(option);
                }
            }
        }

        // Pick the smallest of those big enough. If there is no one big enough, pick the
        // largest of those not big enough.
        if (bigEnough.size() > 0) {
            return Collections.min(bigEnough, new CompareSizesByArea());
        } else if (notBigEnough.size() > 0) {
            return Collections.max(notBigEnough, new CompareSizesByArea());
        } else {
            Log.e(TAG, "Couldn't find any suitable preview size");
            return choices[0];
        }
    }

    public static Camera2BasicFragment newInstance() {
        return new Camera2BasicFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //initLoaction(getActivity());

        View view = inflater.inflate(R.layout.fragment_camera2_basic, container, false);

        Button card_btn = view.findViewById(R.id.scan_card_btn);
        card_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MaterialDialog materialDialog;
                MaterialDialog.Builder builder;
                builder = new MaterialDialog.Builder(getActivity())
                        .title("입력")
                        .content("명함의 전화번호를 입력하세요.")
                        .inputType(InputType.TYPE_CLASS_PHONE)
                        .input(R.string.card_input_hint,R.string.card_input_prefill, new MaterialDialog.InputCallback() {
                            @Override
                            public void onInput(MaterialDialog dialog, CharSequence input) {
                                // Do something
                                phone_num = input.toString();
                                uploadCardData(getActivity());

                            }
                        });
                materialDialog = builder.build();
                materialDialog.show();
            }
        });
        return view;
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        view.findViewById(R.id.picture).setOnClickListener(this);
        mTextureView = (AutoFitTextureView) view.findViewById(R.id.texture);
        materialView = view.getContext();

        callBackListener = (CallBackListener) getActivity();
        lottie = view.findViewById(R.id.lottie_scan_view);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFile = new File(getActivity().getExternalFilesDir(null), "pic.jpg");

    }

    @Override
    public void onResume() {
        super.onResume();
        startBackgroundThread();

        // When the screen is turned off and turned back on, the SurfaceTexture is already
        // available, and "onSurfaceTextureAvailable" will not be called. In that case, we can open
        // a camera and start preview from here (otherwise, we wait until the surface is ready in
        // the SurfaceTextureListener).
        if (mTextureView.isAvailable()) {
            openCamera(mTextureView.getWidth(), mTextureView.getHeight());
        } else {
            mTextureView.setSurfaceTextureListener(mSurfaceTextureListener);
        }
    }

    @Override
    public void onPause() {
        closeCamera();
        stopBackgroundThread();
        super.onPause();
    }

    private void requestCameraPermission() {
        if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
            new ConfirmationDialog().show(getChildFragmentManager(), FRAGMENT_DIALOG);
        } else {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                ErrorDialog.newInstance("camera permission need")
                        .show(getChildFragmentManager(), FRAGMENT_DIALOG);
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    /**
     * Sets up member variables related to camera.
     *
     * @param width  The width of available size for camera preview
     * @param height The height of available size for camera preview
     */
    @SuppressWarnings("SuspiciousNameCombination")
    private void setUpCameraOutputs(int width, int height) {
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            for (String cameraId : manager.getCameraIdList()) {
                CameraCharacteristics characteristics
                        = manager.getCameraCharacteristics(cameraId);

                // We don't use a front facing camera in this sample.
                Integer facing = characteristics.get(CameraCharacteristics.LENS_FACING);
                if (facing != null && facing == CameraCharacteristics.LENS_FACING_FRONT) {
                    continue;
                }

                StreamConfigurationMap map = characteristics.get(
                        CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP);
                if (map == null) {
                    continue;
                }

                // For still image captures, we use the largest available size.
                Size largest = Collections.max(
                        Arrays.asList(map.getOutputSizes(ImageFormat.JPEG)),
                        new CompareSizesByArea());
                mImageReader = ImageReader.newInstance(largest.getWidth(), largest.getHeight(),
                        ImageFormat.JPEG, /*maxImages*/2);
                mImageReader.setOnImageAvailableListener(
                        mOnImageAvailableListener, mBackgroundHandler);

                // Find out if we need to swap dimension to get the preview size relative to sensor
                // coordinate.
                int displayRotation = activity.getWindowManager().getDefaultDisplay().getRotation();
                //noinspection ConstantConditions
                mSensorOrientation = characteristics.get(CameraCharacteristics.SENSOR_ORIENTATION);
                boolean swappedDimensions = false;
                switch (displayRotation) {
                    case Surface.ROTATION_0:
                    case Surface.ROTATION_180:
                        if (mSensorOrientation == 90 || mSensorOrientation == 270) {
                            swappedDimensions = true;
                        }
                        break;
                    case Surface.ROTATION_90:
                    case Surface.ROTATION_270:
                        if (mSensorOrientation == 0 || mSensorOrientation == 180) {
                            swappedDimensions = true;
                        }
                        break;
                    default:
                        Log.e(TAG, "Display rotation is invalid: " + displayRotation);
                }

                Point displaySize = new Point();
                activity.getWindowManager().getDefaultDisplay().getSize(displaySize);
                int rotatedPreviewWidth = width;
                int rotatedPreviewHeight = height;
                int maxPreviewWidth = displaySize.x;
                int maxPreviewHeight = displaySize.y;

                if (swappedDimensions) {
                    rotatedPreviewWidth = height;
                    rotatedPreviewHeight = width;
                    maxPreviewWidth = displaySize.y;
                    maxPreviewHeight = displaySize.x;
                }

                if (maxPreviewWidth > MAX_PREVIEW_WIDTH) {
                    maxPreviewWidth = MAX_PREVIEW_WIDTH;
                }

                if (maxPreviewHeight > MAX_PREVIEW_HEIGHT) {
                    maxPreviewHeight = MAX_PREVIEW_HEIGHT;
                }

                // Danger, W.R.! Attempting to use too large a preview size could  exceed the camera
                // bus' bandwidth limitation, resulting in gorgeous previews but the storage of
                // garbage capture data.
                mPreviewSize = chooseOptimalSize(map.getOutputSizes(SurfaceTexture.class),
                        rotatedPreviewWidth, rotatedPreviewHeight, maxPreviewWidth,
                        maxPreviewHeight, largest);

                // We fit the aspect ratio of TextureView to the size of preview we picked.
                int orientation = getResources().getConfiguration().orientation;
                if (orientation == Configuration.ORIENTATION_LANDSCAPE) {
                    mTextureView.setAspectRatio(
                            mPreviewSize.getWidth(), mPreviewSize.getHeight());
                } else {
                    mTextureView.setAspectRatio(
                            mPreviewSize.getHeight(), mPreviewSize.getWidth());
                }

                // Check if the flash is supported.
                Boolean available = characteristics.get(CameraCharacteristics.FLASH_INFO_AVAILABLE);
                mFlashSupported = available == null ? false : available;

                mCameraId = cameraId;
                return;
            }
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            // Currently an NPE is thrown when the Camera2API is used but not supported on the
            // device this code runs.
            ErrorDialog.newInstance("camera error")
                    .show(getChildFragmentManager(), FRAGMENT_DIALOG);
        }
    }

    /**
     * Opens the camera specified by {@link Camera2BasicFragment#mCameraId}.
     */
    private void openCamera(int width, int height) {
        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
            return;
        }

        //camera pixcel operate !!!!!!!!!!!!!!!!!!!!!!!!!!!중요
        setUpCameraOutputs(width/4, height/4);
        configureTransform(width/4, height/4);
        Activity activity = getActivity();
        CameraManager manager = (CameraManager) activity.getSystemService(Context.CAMERA_SERVICE);
        try {
            if (!mCameraOpenCloseLock.tryAcquire(2500, TimeUnit.MILLISECONDS)) {
                throw new RuntimeException("Time out waiting to lock camera opening.");
            }
            manager.openCamera(mCameraId, mStateCallback, mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera opening.", e);
        }
    }

    /**
     * Closes the current {@link CameraDevice}.
     */
    private void closeCamera() {
        try {
            mCameraOpenCloseLock.acquire();
            if (null != mCaptureSession) {
                mCaptureSession.close();
                mCaptureSession = null;
            }
            if (null != mCameraDevice) {
                mCameraDevice.close();
                mCameraDevice = null;
            }
            if (null != mImageReader) {
                mImageReader.close();
                mImageReader = null;
            }
        } catch (InterruptedException e) {
            throw new RuntimeException("Interrupted while trying to lock camera closing.", e);
        } finally {
            mCameraOpenCloseLock.release();
        }
    }

    /**
     * Starts a background thread and its {@link Handler}.
     */
    private void startBackgroundThread() {
        mBackgroundThread = new HandlerThread("CameraBackground");
        mBackgroundThread.start();
        mBackgroundHandler = new Handler(mBackgroundThread.getLooper());
    }

    /**
     * Stops the background thread and its {@link Handler}.
     */
    private void stopBackgroundThread() {
        mBackgroundThread.quitSafely();
        try {
            mBackgroundThread.join();
            mBackgroundThread = null;
            mBackgroundHandler = null;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a new {@link CameraCaptureSession} for camera preview.
     */
    private void createCameraPreviewSession() {
        try {
            SurfaceTexture texture = mTextureView.getSurfaceTexture();
            assert texture != null;

            // We configure the size of default buffer to be the size of camera preview we want.
            texture.setDefaultBufferSize(mPreviewSize.getWidth(), mPreviewSize.getHeight());

            // This is the output Surface we need to start preview.
            Surface surface = new Surface(texture);

            // We set up a CaptureRequest.Builder with the output Surface.
            mPreviewRequestBuilder
                    = mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_PREVIEW);
            mPreviewRequestBuilder.addTarget(surface);

            // Here, we create a CameraCaptureSession for camera preview.
            mCameraDevice.createCaptureSession(Arrays.asList(surface, mImageReader.getSurface()),
                    new CameraCaptureSession.StateCallback() {

                        @Override
                        public void onConfigured(@NonNull CameraCaptureSession cameraCaptureSession) {
                            // The camera is already closed
                            if (null == mCameraDevice) {
                                return;
                            }

                            // When the session is ready, we start displaying the preview.
                            mCaptureSession = cameraCaptureSession;
                            try {
                                // Auto focus should be continuous for camera preview.
                                mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                                        CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
                                // Flash is automatically enabled when necessary.
                                setAutoFlash(mPreviewRequestBuilder);

                                // Finally, we start displaying the camera preview.
                                mPreviewRequest = mPreviewRequestBuilder.build();
                                mCaptureSession.setRepeatingRequest(mPreviewRequest,
                                        mCaptureCallback, mBackgroundHandler);
                            } catch (CameraAccessException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onConfigureFailed(
                                @NonNull CameraCaptureSession cameraCaptureSession) {
                            showToast("Failed");
                        }
                    }, null
            );
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Configures the necessary {@link Matrix} transformation to `mTextureView`.
     * This method should be called after the camera preview size is determined in
     * setUpCameraOutputs and also the size of `mTextureView` is fixed.
     *
     * @param viewWidth  The width of `mTextureView`
     * @param viewHeight The height of `mTextureView`
     */
    private void configureTransform(int viewWidth, int viewHeight) {
        Activity activity = getActivity();
        if (null == mTextureView || null == mPreviewSize || null == activity) {
            return;
        }
        int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
        Matrix matrix = new Matrix();
        RectF viewRect = new RectF(0, 0, viewWidth, viewHeight);
        RectF bufferRect = new RectF(0, 0, mPreviewSize.getHeight(), mPreviewSize.getWidth());
        float centerX = viewRect.centerX();
        float centerY = viewRect.centerY();
        if (Surface.ROTATION_90 == rotation || Surface.ROTATION_270 == rotation) {
            bufferRect.offset(centerX - bufferRect.centerX(), centerY - bufferRect.centerY());
            matrix.setRectToRect(viewRect, bufferRect, Matrix.ScaleToFit.FILL);
            float scale = Math.max(
                    (float) viewHeight / mPreviewSize.getHeight(),
                    (float) viewWidth / mPreviewSize.getWidth());
            matrix.postScale(scale, scale, centerX, centerY);
            matrix.postRotate(90 * (rotation - 2), centerX, centerY);
        } else if (Surface.ROTATION_180 == rotation) {
            matrix.postRotate(180, centerX, centerY);
        }
        mTextureView.setTransform(matrix);
    }

    /**
     * Initiate a still image capture.
     */
    private void takePicture() {
        lockFocus();
    }

    /**
     * Lock the focus as the first step for a still image capture.
     */
    private void lockFocus() {
        try {
            // This is how to tell the camera to lock focus.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the lock.
            mState = STATE_WAITING_LOCK;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Run the precapture sequence for capturing a still image. This method should be called when
     * we get a response in {@link #mCaptureCallback} from {@link #lockFocus()}.
     */
    private void runPrecaptureSequence() {
        try {
            // This is how to tell the camera to trigger.
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER,
                    CaptureRequest.CONTROL_AE_PRECAPTURE_TRIGGER_START);
            // Tell #mCaptureCallback to wait for the precapture sequence to be set.
            mState = STATE_WAITING_PRECAPTURE;
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Capture a still picture. This method should be called when we get a response in
     * {@link #mCaptureCallback} from both {@link #lockFocus()}.
     */
    private void captureStillPicture() {
        try {
            final Activity activity = getActivity();
            if (null == activity || null == mCameraDevice) {
                return;
            }
            // This is the CaptureRequest.Builder that we use to take a picture.
            final CaptureRequest.Builder captureBuilder =
                    mCameraDevice.createCaptureRequest(CameraDevice.TEMPLATE_STILL_CAPTURE);
            captureBuilder.addTarget(mImageReader.getSurface());

            // Use the same AE and AF modes as the preview.
            captureBuilder.set(CaptureRequest.CONTROL_AF_MODE,
                    CaptureRequest.CONTROL_AF_MODE_CONTINUOUS_PICTURE);
            setAutoFlash(captureBuilder);

            // Orientation
            int rotation = activity.getWindowManager().getDefaultDisplay().getRotation();
            captureBuilder.set(CaptureRequest.JPEG_ORIENTATION, getOrientation(rotation));

            CameraCaptureSession.CaptureCallback CaptureCallback
                    = new CameraCaptureSession.CaptureCallback() {

                @Override
                public void onCaptureCompleted(@NonNull CameraCaptureSession session,
                                               @NonNull CaptureRequest request,
                                               @NonNull TotalCaptureResult result) {
                    //showToast("Saved: " + mFile);
                    Log.d(TAG, mFile.toString());




                    unlockFocus();
                    capture_path = mFile;
//                    Log.d("captured",capture_path);

//                    CallBackListener callBackListener = (CallBackListener) activity;
//                    callBackListener.onSucces("capture");
                }
            };

            mCaptureSession.stopRepeating();
            mCaptureSession.abortCaptures();
            mCaptureSession.capture(captureBuilder.build(), CaptureCallback, null);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    /**
     * Retrieves the JPEG orientation from the specified screen rotation.
     *
     * @param rotation The screen rotation.
     * @return The JPEG orientation (one of 0, 90, 270, and 360)
     */
    private int getOrientation(int rotation) {
        // Sensor orientation is 90 for most devices, or 270 for some devices (eg. Nexus 5X)
        // We have to take that into account and rotate JPEG properly.
        // For devices with orientation of 90, we simply return our mapping from ORIENTATIONS.
        // For devices with orientation of 270, we need to rotate the JPEG 180 degrees.
        return (ORIENTATIONS.get(rotation) + mSensorOrientation + 270) % 360;
    }

    /**
     * Unlock the focus. This method should be called when still image capture sequence is
     * finished.
     */
    private void unlockFocus() {
        try {
            // Reset the auto-focus trigger
            mPreviewRequestBuilder.set(CaptureRequest.CONTROL_AF_TRIGGER,
                    CameraMetadata.CONTROL_AF_TRIGGER_CANCEL);
            setAutoFlash(mPreviewRequestBuilder);
            mCaptureSession.capture(mPreviewRequestBuilder.build(), mCaptureCallback,
                    mBackgroundHandler);
            // After this, the camera will go back to the normal state of preview.
            mState = STATE_PREVIEW;
            mCaptureSession.setRepeatingRequest(mPreviewRequest, mCaptureCallback,
                    mBackgroundHandler);
        } catch (CameraAccessException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.picture: {
                takePicture();
                lottie.setVisibility(View.VISIBLE);
                lottie.loop(true);
                lottie.playAnimation();
                break;
            }
            case R.id.info: {
                Activity activity = getActivity();
                if (null != activity) {
                    new AlertDialog.Builder(activity)
                            .setMessage("안녕하세요")
                            .setPositiveButton(android.R.string.ok, null)
                            .show();
                }
                break;
            }
        }
    }

    public void uploadCardData(Activity activity){
        RequestManager requestManager = new RequestManager();
        final SharedDataManager mDBManager = SharedDataManager.getInstance();

        try{
            requestManager.getCardIdToDjango(phone_num, new RequestManager.DjangoImageUploadCallback() {
                @Override
                public void onCallback(JSONObject response) {
                    try{
                        marker_url = response.getString("card_url");
                        card_id = response.getString("card_id");
                        if(marker_url.equals("null") || marker_id.equals("null")){
                            showToast("명함을 찾지 못하였습니다...");
                        }else {

                            //Log.d("contentId_check",contents_id+"111");
                            callBackListener.onSuccess("upload",false);
                        }
                    }catch (JSONException e){
                        e.printStackTrace();
                        showToast("명함을 찾지 못하였습니다...");
                    }

                }
            });
        }catch (JSONException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            showToast("서버 연결에 실패하였습니다.");
            e.printStackTrace();
        }
    }

    public void uploadData(Activity activity) {

        RequestManager requestManager = new RequestManager();
        final SharedDataManager mDBManager = SharedDataManager.getInstance();

            try{
                Log.d("request","started");
                requestManager.uploadImageToDjango(mFile,mDBManager.currentLatitude,mDBManager.currentLongtitude, new RequestManager.DjangoImageUploadCallback() {
                    @Override
                    public void onCallback(JSONObject response) {

                        new Thread()
                        {
                            public void run()
                            {
                                Message message = handler.obtainMessage();
                                handler.sendMessage(message);
                            }
                        }.start();

                        try{
                            Log.d("Image upload Result:",response.toString());
                            marker_url = response.getString("marker_url");
                            marker_id = response.getString("marker_id");
                            if(marker_url.equals("null") || marker_id.equals("null")){
                                showToast("마커를 찾지 못하였습니다...");
                            }else {

                                //Log.d("contentId_check",contents_id+"111");
                                callBackListener.onSuccess("upload",true);
                            }
                        }catch (JSONException e){
                            e.printStackTrace();
                        }catch (NullPointerException e){
                            showToast("마커를 찾지 못하였습니다...");
                        }
                    }
                });
            }catch (JSONException e) {
                e.printStackTrace();
            }catch (NullPointerException e){
                showToast("서버 연결에 실패하였습니다.");
                e.printStackTrace();
            }
        }

    public String getMarkerUrl(){
        return this.marker_url;
    }

    public void initLoaction(Activity activity){
//        showDialog("위치를 가져오는 중입니다.");
        lm = (LocationManager) activity.getSystemService(Context.LOCATION_SERVICE);
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(activity);

        try {
            lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
            lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, // 등록할 위치제공자
                    100, // 통지사이의 최소 시간간격 (miliSecond)
                    1, // 통지사이의 최소 변경거리 (m)
                    mLocationListener);
        }catch (SecurityException e){
            e.printStackTrace();
        }
    }

    private final LocationListener mLocationListener = new LocationListener() {
        public void onLocationChanged(Location location) {
            //if location data is change, listener event alert.
            SharedDataManager mDBManager = SharedDataManager.getInstance();

            double longitude = location.getLongitude(); //경도
            double latitude = location.getLatitude();   //위도
            Log.d("long",longitude+"");
            Log.d("lati",latitude+"");
            //        double altitude = location.getAltitude();   //고도//          float accuracy = location.getAccuracy();    //정확도//            String provider = location.getProvider();   //위치제공자

            try{
                LatLng currentLocation = new LatLng(latitude,longitude);
                MarkerOptions markerOptions = new MarkerOptions();
                markerOptions.position(currentLocation);
                //markerOptions.title("현재 위치");
                //markerOptions.snippet("ARZone");
                //mMap.addMarker(markerOptions);
                lm.removeUpdates(mLocationListener);  //  미수신할때는 반드시 자원해체를 해주어야 한다.
                mDBManager.currentLongtitude = longitude;
                mDBManager.currentLatitude = latitude;
                Log.d("latitude",mDBManager.currentLatitude+"");
                Log.d("longitude",mDBManager.currentLongtitude+"");


                //materialDialog.dismiss();
                //MarkerUploader uploader = new MarkerUploader(getActivity());
                //uploader.start(false);

            }catch (NullPointerException e){
                e.printStackTrace();
            }
        }

        public void onProviderDisabled(String provider) {
            // Disabled시
            Log.d("test", "onProviderDisabled, provider:" + provider);
        }

        public void onProviderEnabled(String provider) {
            // Enabled시
            Log.d("test", "onProviderEnabled, provider:" + provider);
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
            // 변경시
            Log.d("test", "onStatusChanged, provider:" + provider + ", status:" + status + " ,Bundle:" + extras);
        }
    };


    private void getMarkerData(String marker_id){
        //Todo...
        showToast(marker_id);
    }

    private void setAutoFlash(CaptureRequest.Builder requestBuilder) {
        if (mFlashSupported) {
            requestBuilder.set(CaptureRequest.CONTROL_AE_MODE,
                    CaptureRequest.CONTROL_AE_MODE_ON_AUTO_FLASH);
        }


    }



    /**
     * Saves a JPEG {@link Image} into the specified {@link File}.
     */
    private static class ImageSaver implements Runnable {

        /**
         * The JPEG image
         */
        private final Image mImage;
        /**
         * The file we save the image into.
         */
        private final File mFile;
        private Activity activity;

        CallBackListener callBackListener = (CallBackListener) activity;

        ImageSaver(Image image, File file, Activity activity) {
            mImage = image;
            mFile = file;
            this.activity = activity;
        }

        @Override
        public void run() {
            MaterialDialog materialDialog;
            MaterialDialog.Builder builder;
            builder = new MaterialDialog.Builder(activity)
                    .title("요청")
                    .content("이미지 스캔중...")
                    .progress(true,0);
            materialDialog = builder.build();
            //materialDialog.show();

            ByteBuffer buffer = mImage.getPlanes()[0].getBuffer();
            byte[] bytes = new byte[buffer.remaining()];
            buffer.get(bytes);
            FileOutputStream output = null;
            try {
                output = new FileOutputStream(mFile);
                output.write(bytes);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                mImage.close();
                //materialDialog.dismiss();

                if (null != output) {
                    try {

                        output.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }



    }

    /**
     * Compares two {@code Size}s based on their areas.
     */
    static class CompareSizesByArea implements Comparator<Size> {

        @Override
        public int compare(Size lhs, Size rhs) {
            // We cast here to ensure the multiplications won't overflow
            return Long.signum((long) lhs.getWidth() * lhs.getHeight() -
                    (long) rhs.getWidth() * rhs.getHeight());
        }

    }

    /**
     * Shows an error message dialog.
     */
    public static class ErrorDialog extends DialogFragment {

        private static final String ARG_MESSAGE = "message";

        public static ErrorDialog newInstance(String message) {
            ErrorDialog dialog = new ErrorDialog();
            Bundle args = new Bundle();
            args.putString(ARG_MESSAGE, message);
            dialog.setArguments(args);
            return dialog;
        }

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Activity activity = getActivity();
            return new AlertDialog.Builder(activity)
                    .setMessage(getArguments().getString(ARG_MESSAGE))
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            activity.finish();
                        }
                    })
                    .create();
        }



    }

    /**
     * Shows OK/Cancel confirmation dialog about camera permission.
     */
    public static class ConfirmationDialog extends DialogFragment {

        @NonNull
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            final Fragment parent = getParentFragment();
            return new AlertDialog.Builder(getActivity())
                    .setMessage("camera permission need")
                    .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            parent.requestPermissions(new String[]{Manifest.permission.CAMERA},
                                    REQUEST_CAMERA_PERMISSION);
                        }
                    })
                    .setNegativeButton(android.R.string.cancel,
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Activity activity = parent.getActivity();
                                    if (activity != null) {
                                        activity.finish();
                                    }
                                }
                            })
                    .create();
        }
    }

    public void showDialog(String msg,Activity activity){

        builder = new MaterialDialog.Builder(activity)
                .title("요청")
                .content(msg)
                .progress(true,0);
        materialDialog = builder.build();
        materialDialog.show();
    }

    public void getMarkerModel(final CallBackListener callBackListener){
        Log.e("marker get","try");
        RequestManager requestManager = RequestManager.getInstance();
        requestManager.requestGetMarkerInfo(marker_id, new RequestManager.MarkerCallback() {
            @Override
            public void onResponse(MarkerModel response) {
                setContentsInfo(response);
                callBackListener.onSuccess("getMarkerModel",true);
                Log.e("marker get","sucess");
            }
        });
    }
    public void getCardModel(final CallBackListener callBackListener){
        Log.e("card get","try");
        RequestManager requestManager = RequestManager.getInstance();
        requestManager.requestGetCardInfo(card_id, new RequestManager.CardCallback() {
            @Override
            public void onResponse(BusinessCardModel response) {
                setCardContentsInfo(response);
                callBackListener.onSuccess("getMarkerModel",false);
                Log.e("card get","sucess");
            }
        });
    }

    public void setCardContentsInfo(BusinessCardModel cardModel){
        String contentId = cardModel.getContentId();
        ArrayList<Float> contentRota = cardModel.getContentRotation();
        float contentScale = cardModel.getScale();
        String markerUrl =  cardModel.getFile();
        SharedDataManager mDBManager = SharedDataManager.getInstance();
        mDBManager.contentId = contentId;
        mDBManager.contentRotation = contentRota;
        mDBManager.contentScale = contentScale;
        marker_url = markerUrl;

//        Log.e("c_id",contentId);
//        Log.e("c_scale",contentScale+"");
//        Log.e("c_url",markerUrl);
//        Log.e("c_rotate",contentRota.toString());
    }


    public void setContentsInfo(MarkerModel markerModel){
        String contentId = markerModel.getContentId();
        ArrayList<Float> contentRota = markerModel.getContentRotation();
        float contentScale = markerModel.getScale();
        String markerUrl =  markerModel.getFile();
        SharedDataManager mDBManager = SharedDataManager.getInstance();
        mDBManager.contentId = contentId;
        mDBManager.contentRotation = contentRota;
        mDBManager.contentScale = contentScale;
        marker_url = markerUrl;

//        Log.e("c_id",contentId);
//        Log.e("c_scale",contentScale+"");
//        Log.e("c_url",markerUrl);
//        Log.e("c_rotate",contentRota.toString());
    }

    public void getContentsModel(final CallBackListener callBackListener){
        final String contentId = SharedDataManager.getInstance().contentId;
        RequestManager requestManager = RequestManager.getInstance();


        requestManager.requestGetContentInfo(contentId, new RequestManager.ContentCallback() {
            @Override
            public void onResponse(ContentModel response) {
                contentModel = response;
                callBackListener.onSuccess("contentsModel",true);
            }
        });
    }


    public void getTextures(final CallBackListener callBackListener){
        try{
            Log.d("markertest",contentModel.toString());

            for(int i=0;i<contentModel.getTextures().size();i++){
                Log.d("texture real name",contentModel.getTextures().get(i)+"");
                String texture_url = contentModel.getTextures().get(i);
                getTexture(contentModel.getContentName(),texture_url,i,contentModel.getTextures().size(),callBackListener);
            }
        }catch (NullPointerException e){
            e.printStackTrace();
        }
    }

    public void getTexture(final String name, final String url, final int request_count, final int last_count,final CallBackListener callBackListener){
        try {
            String suffix = url.substring(url.indexOf('.'), url.length());
            Log.d("texture_request_suffix",suffix);
            Log.d("texture_request_url",url);
            RequestManager mRequestManager = RequestManager.getInstance();
            mRequestManager.requestDownloadFileFromStorage(name, url, suffix, new RequestManager.TransferCallback() {
                @Override
                public void onResponse(TransferModel response) {
                    if (response.getSuffix().compareTo(".jpg") == 0 || response.getSuffix().compareTo(".png") == 0) {
                        Bitmap downBitmap = BitmapFactory.decodeFile(response.getPath());
                        //imgView.setImageBitmap(downBitmap);

                        //String texture_file_name=response.getPath().substring(response.getPath().lastIndexOf("/")+1,response.getPath().length()-4);
                        String texture_file_name = url.substring(url.lastIndexOf("/")+1,url.length()-4);
                        Log.d("getTexture_name",texture_file_name);
                        saveBitmaptoJpeg(downBitmap,name,texture_file_name,false);
                    }
                    Log.d(TAG, "onResponse: content download complete ");
                    String texture_url = response.getPath();
                    //Log.d("texture_path",texture_url);
                    //textures.add(texture_url);
                    //setup();

                    //very important
                    textureCount++;
                    if(textureCount == last_count){
                        callBackListener.onSuccess("textures",true);
                    }

                }
            });
        }catch (StringIndexOutOfBoundsException e){e.printStackTrace();}
    }

    public void getMarker(final CallBackListener cameraActivity) {
        final String url = marker_url;
        String suffix = url.substring(url.indexOf('.'), url.length());
        final String name = "markers";
        RequestManager mRequestManager = RequestManager.getInstance();
        mRequestManager.requestDownloadFileFromStorage(name, url, suffix, new RequestManager.TransferCallback() {
            @Override
            public void onResponse(TransferModel response) {
                if (response.getSuffix().compareTo(".jpg") == 0 || response.getSuffix().compareTo(".png") == 0) {
                    Bitmap downBitmap = BitmapFactory.decodeFile(response.getPath());
                    //imgView.setImageBitmap(downBitmap);

                    //String texture_file_name=response.getPath().substring(response.getPath().lastIndexOf("/")+1,response.getPath().length()-4);
                    String texture_file_name = url.substring(url.lastIndexOf("/")+1,url.length()-4);
                    Log.d("getTexture_name",texture_file_name);

                    // name to be folder name
                    saveBitmaptoJpeg(downBitmap,name,texture_file_name,true);
                    if(hadMarker){
                        cameraActivity.onSuccess("textures",true);
                    }else {
                        cameraActivity.onSuccess("markerImg",true);
                    }

                }
            }
        });
    }


    public void getModelFromStorage(final CallBackListener callBackListener){
        String url = contentModel.getModel();
        String suffix = url.substring(url.indexOf('.'), url.length());
        RequestManager mRequestManager = RequestManager.getInstance();
        mRequestManager.requestDownloadFileFromStorage(contentModel.getContentName(), url, suffix, new RequestManager.TransferCallback() {
            @Override
            public void onResponse(TransferModel response) {
                try{
                    if (response.getSuffix().compareTo(".jet") == 0)
                    {
                        //modelUrl = response.getPath();

                        //String model_file_name = response.getPath().substring(response.getPath().lastIndexOf("/") + 1, response.getPath().length() - 4);
    //                    Log.d("model file name:",contentModel.getContentName());
                        String model_file_name = contentModel.getContentName();
                        Log.d("getModel_name", model_file_name);
                        final FileInputStream in = new FileInputStream(response.getPath());
                        saveTemptoJet(in, contentModel.getContentName(), model_file_name);
                        callBackListener.onSuccess("model",true);
                    }else if(response.getSuffix().compareTo(".jpg") == 0 || response.getSuffix().compareTo(".png") == 0) {
                        String model_file_name = contentModel.getContentName();
                        Log.d("getModel_name", model_file_name);
                            final FileInputStream in = new FileInputStream(response.getPath());
                            saveModelFile(in, contentModel.getContentName(), model_file_name, response.getSuffix());
                        callBackListener.onSuccess("textures",true);
                    }else if (response.getSuffix().compareTo(".mp4") == 0) {
                        String model_file_name = contentModel.getContentName();
                        Log.d("getModel_name", model_file_name);
                        final FileInputStream in = new FileInputStream(response.getPath());
                        saveModelFile(in, contentModel.getContentName(), model_file_name, response.getSuffix());
                        callBackListener.onSuccess("textures",true);
                    }
                }catch(FileNotFoundException e){
                    e.printStackTrace();
                }



            }
        });
    }
    public void saveTemptoJet(InputStream in,String folder, String name){
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath(); // Get Absolute Path in External Sdcard
        String foler_name = "/kudan/"+folder+"/";
        String file_name = name+".jet";
        String string_path = ex_storage+foler_name;
        File out_file_path=null;

        File check_files = new File(string_path+file_name);
        if(check_files.exists()==true) {
            modelUrl = string_path+file_name;
        }else {
            try{
                out_file_path = new File(string_path);
                if(!out_file_path.isDirectory()){
                    out_file_path.mkdirs();
                }

                BufferedInputStream bis = new BufferedInputStream(in);
                FileOutputStream fos = new FileOutputStream(string_path+file_name);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                int data = 0;

                final byte[] buffer = new byte[1024];
                while ((data = bis.read()) != -1){
                    bos.write(data);
                }

//            BufferedInputStream bIS = new BufferedInputStream(in);
                bis.close();
                in.close();
                bos.close();
                fos.close();
//            textures.add(string_path+file_name);
                modelUrl = string_path+file_name;
                Log.d("model_path",string_path+file_name);

            }catch(FileNotFoundException exception){
                Log.e("FileNotFoundException", exception.getMessage());
            }catch(IOException exception){
                Log.e("IOException", exception.getMessage());
            }
        }
    }

    public void saveBitmaptoJpeg(Bitmap bitmap, String folder, String name, boolean isMarker){
        String ex_storage =Environment.getExternalStorageDirectory().getAbsolutePath(); // Get Absolute Path in External Sdcard
        String foler_name = "/kudan/"+folder+"/";
        String file_name = name+".jpg";
        String string_path = ex_storage+foler_name;
        File file_path;
        File files = new File(string_path+file_name);
        //파일 유무를 확인합니다.
        if(files.exists()==true) {
        //파일이 있을시
            if(isMarker){
                SharedDataManager.getInstance().imageURI = Uri.fromFile(new File(string_path+file_name));
            }else {
                textures.add(string_path + file_name);
            }

        } else {
        //파일이 없을시
            try{
                file_path = new File(string_path);
                if(!file_path.isDirectory()){
                    file_path.mkdirs();
                }
                FileOutputStream out = new FileOutputStream(string_path+file_name);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
                out.close();
                if(isMarker){
                    SharedDataManager.getInstance().imageURI = Uri.fromFile(new File(string_path+file_name));
                }else {
                    textures.add(string_path + file_name);
                }
                //Log.d("textures_path",string_path+file_name);
            }catch(FileNotFoundException exception){
                Log.e("FileNotFoundException", exception.getMessage());
            }catch(IOException exception){
                Log.e("IOException", exception.getMessage());
            }
        }


    }

    public void saveTemptoMp4(FileInputStream fis,String folder, String name){
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath(); // Get Absolute Path in External Sdcard
        String foler_name = "/kudan/"+folder+"/";
        String file_name = name+".mp4";
        String string_path = ex_storage+foler_name;
        File file_path;
        try{
            file_path = new File(string_path);
            if(!file_path.isDirectory()){
                file_path.mkdirs();
            }

            BufferedInputStream bis = new BufferedInputStream(fis);
            FileOutputStream fos = new FileOutputStream(string_path+file_name);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            int data = 0;

            while ((data = bis.read()) != -1){
                bos.write(data);
            }

            bis.close();
            fis.close();
            bos.close();
            fos.close();
            modelUrl = string_path+file_name;
            Log.d("model_path",string_path+file_name);

        }catch(FileNotFoundException exception){
            Log.e("FileNotFoundException", exception.getMessage());
        }catch(IOException exception){
            Log.e("IOException", exception.getMessage());
        }
    }

    public void saveModelFile(FileInputStream fis,String folder, String name,String extension){
        String ex_storage = Environment.getExternalStorageDirectory().getAbsolutePath(); // Get Absolute Path in External Sdcard
        String foler_name = "/kudan/"+folder+"/";
        String file_name = name+"."+extension;
        String string_path = ex_storage+foler_name;
        File file_path;


        File checkFile = new File(string_path+file_name);

        if(checkFile.exists()){
            modelUrl = string_path+file_name;
        }else {
            try{
                file_path = new File(string_path);
                if(!file_path.isDirectory()){
                    file_path.mkdirs();
                }

                BufferedInputStream bis = new BufferedInputStream(fis);
                FileOutputStream fos = new FileOutputStream(string_path+file_name);
                BufferedOutputStream bos = new BufferedOutputStream(fos);
                int data = 0;

                while ((data = bis.read()) != -1){
                    bos.write(data);
                }

                bis.close();
                fis.close();
                bos.close();
                fos.close();
                modelUrl = string_path+file_name;
                Log.d("model_path",string_path+file_name);

            }catch(FileNotFoundException exception){
                Log.e("FileNotFoundException", exception.getMessage());
            }catch(IOException exception){
                Log.e("IOException", exception.getMessage());
            }
        }

    }

    public void setContentsModel() {
        contentModel_putExtra = new ContentModel();
        contentModel_putExtra.setTextures(textures);
        contentModel_putExtra.setModel(modelUrl);
    }
}

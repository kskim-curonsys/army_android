package com.curonsys.army_android.util;

import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.curonsys.army_android.model.ContentModel;
import com.curonsys.army_android.model.MarkerModel;
import com.curonsys.army_android.model.OrderModel;
import com.curonsys.army_android.model.ProductModel;
import com.curonsys.army_android.model.ShopModel;
import com.curonsys.army_android.model.TransferModel;
import com.curonsys.army_android.model.UserModel;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Transaction;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.curonsys.army_android.util.Constants.STORAGE_BASE_URL;

/**
 * Created by ijin-yeong on 2018. 7. 23..
 */

public class RequestManager {
    private static final String TAG = RequestManager.class.getSimpleName();

    public static final int CATEGORY_USER = 0;
    public static final int CATEGORY_IMAGE = 1;
    public static final int CATEGORY_MARKER = 2;
    public static final int CATEGORY_MODEL = 3;

    private static String mBaseUrl = "https://kskim-curonsys.com";

    private static RequestManager mInstance;
    public static RequestManager getInstance() {
        if (mInstance == null) {
            mInstance = new RequestManager();
        }
        return mInstance;
    }

    private FirebaseFirestore mFirestore;
    private ListenerRegistration mListenerOrders;
    private FirebaseStorage mStorage;
    UploadTask mUploadTask;

    public interface UserCallback {
        public void onResponse(UserModel response);
    }

    public interface ContentCallback {
        public void onResponse(ContentModel response);
    }

    public interface ContentsListCallback {
        public void onResponse(ArrayList<ContentModel> response);
    }

    public interface MarkerCallback {
        public void onResponse(MarkerModel response);
    }

    public interface MarkerListCallback {
        public void onResponse(ArrayList<MarkerModel> response);
    }

    public interface TransferCallback {
        public void onResponse(TransferModel response);
    }

    public interface SuccessCallback {
        public void onResponse(boolean success);
    }

    public interface ShopCallback {
        public void onResponse(ShopModel response);
    }

    public interface ProductCallback {
        public void onResponse(ProductModel response);
    }

    public interface ProductListCallback {
        public void onResponse(ArrayList<ProductModel> response);
    }

    public interface OrderCallback {
        public void onResponse(OrderModel response);
    }

    public interface OrderListCallback {
        public void onResponse(ArrayList<OrderModel> response);
    }

    public RequestManager() {
        mFirestore = FirebaseFirestore.getInstance();
        mStorage = FirebaseStorage.getInstance(STORAGE_BASE_URL);
    }

    public RequestManager(String baseurl) {
        mBaseUrl = baseurl;
        mFirestore = FirebaseFirestore.getInstance();
    }

    public void requestGetUserInfo(String userid, final UserCallback callback) {
        DocumentReference docRef = mFirestore.collection("users").document(userid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                UserModel model = new UserModel(documentSnapshot.getData());
                callback.onResponse(model);
            }
        });
    }

    public void requestSetUserInfo(UserModel data, final SuccessCallback callback) {
        DocumentReference docRef;
        if (data.getUserId().isEmpty()) {
            docRef = mFirestore.collection("users").document();
            String docid = docRef.getId();
            data.setUserId(docid);

        } else {
            docRef = mFirestore.collection("users").document(data.getUserId());
        }

        docRef.set(data.getData())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "User data successfully written!");
                        callback.onResponse(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing user document", e);
                        callback.onResponse(false);
                    }
                });
    }

    public void requestGetContentInfo(String contentid, final ContentCallback callback) {
        DocumentReference docRef = mFirestore.collection("models").document(contentid);

        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                ContentModel model = new ContentModel(documentSnapshot.getData());
                callback.onResponse(model);
            }
        });
    }

    public void requestGetContentsList(ArrayList<String> ids, final ContentsListCallback callback) {
        // get all docs, compare each doc's id
        // - todo: If there are so many items, ..?  (multiple limit? or Query?)
        mFirestore.collection("models")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<ContentModel> list = new ArrayList<ContentModel>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                if (ids.contains(document.getId())) {
                                    ContentModel model = new ContentModel(document.getData());
                                    list.add(model);
                                }
                            }
                            callback.onResponse(list);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void requestGetAllContents(final ContentsListCallback callback) {
        mFirestore.collection("models")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<ContentModel> list = new ArrayList<ContentModel>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                ContentModel model = new ContentModel(document.getData());
                                list.add(model);
                            }
                            callback.onResponse(list);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void requestSetContentInfo(ContentModel data, final SuccessCallback callback) {
        DocumentReference docRef;
        if (data.getContentId().isEmpty()) {
            docRef = mFirestore.collection("models").document();
            String docid = docRef.getId();
            data.setContentId(docid);

        } else {
            docRef = mFirestore.collection("models").document(data.getContentId());
        }

        docRef.set(data.getData())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Content data successfully written!");
                        callback.onResponse(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing content document", e);
                        callback.onResponse(false);
                    }
                });
    }

    public void requestGetMarkerList(LatLng location, String city, final MarkerListCallback callback) {
        mFirestore.collection("markers")
                .whereEqualTo("city", city)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            ArrayList<MarkerModel> list = new ArrayList<MarkerModel>();

                            for (QueryDocumentSnapshot document : task.getResult()) {
                                MarkerModel data = new MarkerModel(document.getData());
                                list.add(data);
                                Log.d(TAG, document.getId() + " => " + document.getData());
                            }
                            callback.onResponse(list);

                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                        }
                    }
                });
    }

    public void requestGetMarkerInfo(String markerid, final MarkerCallback callback) {
        DocumentReference docRef = mFirestore.collection("markers").document(markerid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                Log.d(TAG, documentSnapshot.getId() + " => " + documentSnapshot.getData());
                MarkerModel model = new MarkerModel(documentSnapshot.getData());
                callback.onResponse(model);
            }
        });
    }

    public void requestSetMarkerInfo(MarkerModel marker, final SuccessCallback callback) {
        DocumentReference docRef;
        if (marker.getMarkerId().isEmpty()) {
            docRef = mFirestore.collection("markers").document();
            String docid = docRef.getId();
            marker.setMarkerId(docid);

        } else {
            docRef = mFirestore.collection("markers").document(marker.getMarkerId());
        }

        docRef.set(marker.getData())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Marker data successfully written!");
                        callback.onResponse(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error writing marker document", e);
                        callback.onResponse(false);
                    }
                });
    }

    public void requestRunTransaction(/* ... */ String userid, final SuccessCallback callback) {
        // temp
        final DocumentReference sfDocRef = mFirestore.collection("users").document(userid);

        mFirestore.runTransaction(new Transaction.Function<Void>() {
            @Override
            public Void apply(Transaction transaction) throws FirebaseFirestoreException {
                DocumentSnapshot snapshot = transaction.get(sfDocRef);
                //double newPopulation = snapshot.getDouble("population") + 1;
                //transaction.update(sfDocRef, "population", newPopulation);
                // Success
                return null;
            }
        }).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                Log.d(TAG, "Transaction success!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Transaction failure.", e);
            }
        });
    }

    public void requestRunBatch(/* ... */ String userid, final SuccessCallback callback) {
        // temp
        WriteBatch batch = mFirestore.batch();

        DocumentReference nycRef = mFirestore.collection("cities").document("NYC");
        batch.set(nycRef, new MarkerModel());

        DocumentReference sfRef = mFirestore.collection("cities").document("SF");
        batch.update(sfRef, "population", 1000000L);

        DocumentReference laRef = mFirestore.collection("cities").document("LA");
        batch.delete(laRef);

        batch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // ...
                callback.onResponse(true);
            }
        });
    }

    public void requestStartCheckUpdate(String userid, final OrderListCallback callback) {
        mListenerOrders = mFirestore.collection("orders")
                .whereEqualTo("status", "order")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot snapshots,
                                          @Nullable FirebaseFirestoreException e) {
                        if (e != null) {
                            Log.w(TAG, "Listen failed.", e);
                            return;
                        }

                        ArrayList<OrderModel> orders = new ArrayList<OrderModel>();
                        for (QueryDocumentSnapshot doc : snapshots) {
                            OrderModel model = new OrderModel(doc.getData());
                            orders.add(model);
                            callback.onResponse(orders);
                        }
                    }
                });
    }

    public void requestStopCheckUpdate(/* ... */) {
        if (mListenerOrders != null) {
            mListenerOrders.remove();
        }
    }

    public void requestDownloadFileFromStorage(String name, String path, String suffix, final TransferCallback callback) {
        StorageReference downRef = mStorage.getReference(path);
        try {
            File localFile = File.createTempFile(name, suffix);
            downRef.getFile(localFile).addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                    Log.d(TAG, "onSuccess: file download success (" + taskSnapshot.getTotalByteCount() + ", " + localFile.getAbsolutePath() + ")");
                    Map<String, Object> values = new HashMap<>();
                    values.put("path", localFile.getAbsolutePath());
                    values.put("suffix", suffix);
                    values.put("size", taskSnapshot.getTotalByteCount());

                    TransferModel data = new TransferModel(values);
                    callback.onResponse(data);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    Log.d(TAG, "onFailure: file download failed (" + exception.getMessage() + ")");
                    Map<String, Object> values = new HashMap<>();
                    values.put("error", exception.getMessage());

                    TransferModel data = new TransferModel(values);
                    callback.onResponse(data);
                }
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void requestUploadFileToStorage(TransferModel model, int category, final TransferCallback callback) {
        Uri uri = Uri.fromFile(new File(model.getPath()));
        StorageReference ref = mStorage.getReference();
        StorageReference upRef = ref.child("images/" + model.getName());

        mUploadTask = upRef.putFile(uri);
        mUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                callback.onResponse(null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageMetadata meta = taskSnapshot.getMetadata();

                Map<String, Object> values = new HashMap<>();
                values.put("path", meta.getPath());
                String url = meta.getPath();
                String suffix = url.substring(url.indexOf('.'), url.length());
                values.put("suffix", suffix);
                values.put("content_type", meta.getContentType());
                values.put("name", meta.getName());
                values.put("md5hash", meta.getMd5Hash());
                values.put("size", meta.getSizeBytes());
                values.put("creation_time", meta.getCreationTimeMillis());
                values.put("updated_time", meta.getUpdatedTimeMillis());
                TransferModel result = new TransferModel(values);

                callback.onResponse(result);
            }
        });
    }

    public void requestUploadFileToStorage(TransferModel model, Uri uri, int category, final TransferCallback callback) {
        StorageReference ref = mStorage.getReference();
        StorageReference upRef;
        if (category == CATEGORY_USER) {
            upRef = ref.child("users/" + model.getUserId() + "/" + model.getName());

        } else if (category == CATEGORY_IMAGE) {
            upRef = ref.child("images/" + model.getName());

        } else if (category == CATEGORY_MARKER) {
            upRef = ref.child("markers/" + model.getName());

        } else if (category == CATEGORY_MODEL) {
            upRef = ref.child("models/" + model.getName());

        } else {
            return;
        }

        mUploadTask = upRef.putFile(uri);
        mUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                callback.onResponse(null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageMetadata meta = taskSnapshot.getMetadata();

                Map<String, Object> values = new HashMap<>();
                values.put("path", meta.getPath());
                String url = meta.getPath();
                String suffix = url.substring(url.indexOf('.'), url.length());
                values.put("suffix", suffix);
                values.put("content_type", meta.getContentType());
                values.put("name", meta.getName());
                values.put("md5hash", meta.getMd5Hash());
                values.put("size", meta.getSizeBytes());
                values.put("creation_time", meta.getCreationTimeMillis());
                values.put("updated_time", meta.getUpdatedTimeMillis());
                TransferModel result = new TransferModel(values);

                callback.onResponse(result);
            }
        });
    }

    public void requestUploadMarkerToStorage(TransferModel model, Uri uri, Map<String, Object> address, final TransferCallback callback) {
        StorageReference ref = mStorage.getReference();
        StorageReference upRef = ref.child("markers/" + address.get("country_code") + "/" + address.get("locality") + "/" + model.getName());

        mUploadTask = upRef.putFile(uri);
        mUploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                callback.onResponse(null);
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                StorageMetadata meta = taskSnapshot.getMetadata();

                Map<String, Object> values = new HashMap<>();
                values.put("path", meta.getPath());
                String url = meta.getPath();
                String suffix = url.substring(url.indexOf('.'), url.length());
                values.put("suffix", suffix);
                values.put("content_type", meta.getContentType());
                values.put("name", meta.getName());
                values.put("md5hash", meta.getMd5Hash());
                values.put("size", meta.getSizeBytes());
                values.put("creation_time", meta.getCreationTimeMillis());
                values.put("updated_time", meta.getUpdatedTimeMillis());
                TransferModel result = new TransferModel(values);

                callback.onResponse(result);
            }
        });
    }

    // shop info
    public void requestGetShopInfo(String shop_id) {

    }

    // product list
    public void requestGetProductList(String shop_id) {

    }

    // order list
    public void requestGetOrderList(String shop_id) {
    }

    // update order info
    public void requestSetOrderInfo() {

    }

}

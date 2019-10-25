package org.michiganhackers.photoassassin;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

import static android.app.Activity.RESULT_OK;

// Activities that use RequestImageDialog must call RequestImageDialog.OnActivityResult in the
// activity's onActivityResult method
// Note that the same filename is always used if a photo is taken
public class RequestImageDialog extends AppCompatDialogFragment {

    private File tempPhotoFile;

    private static final int PICK_IMAGE = 1;
    private static final int TAKE_PICTURE = 2;
    private final String TAG = getClass().getCanonicalName();

    public interface ImageUriHandler {
        void handleImageUri(Uri uri);
    }

    private ImageUriHandler imageUriHandler;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            imageUriHandler = (ImageUriHandler) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(requireActivity().toString()
                    + " must implement ImageUriHandler");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());

        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.request_image_dialog, null);

        builder.setView(view);

        LinearLayout takeNewPhotoButton = view.findViewById(R.id.button_take_new_photo);
        takeNewPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    try {
                        tempPhotoFile = new File(requireActivity().getFilesDir(), "photoassassin.tempPhoto.jpg");
                        if (!tempPhotoFile.createNewFile()) {
                            Log.d(TAG, "tempPhotoFile already exists");
                        }

                        Uri tempPhotoContentUri = FileProvider.getUriForFile(requireActivity(),
                                BuildConfig.APPLICATION_ID + ".provider",
                                tempPhotoFile);
                        takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, tempPhotoContentUri);
                        requireActivity().startActivityForResult(takePictureIntent, TAKE_PICTURE);
                    } catch (IOException e) {
                        Log.e(TAG, "Error while creating tempPhoto file", e);
                    }
                } else {
                    Toast.makeText(requireActivity(), requireActivity().getString(R.string.no_photo_app_found), Toast.LENGTH_LONG).show();
                    Log.w(TAG, "User's device does not have app that takes pictures");
                }
                dismiss();
            }
        });

        LinearLayout selectFromGalleryButton = view.findViewById(R.id.button_select_from_gallery);
        selectFromGalleryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent pickImageIntent = new Intent(Intent.ACTION_PICK,
                        android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                if (pickImageIntent.resolveActivity(requireActivity().getPackageManager()) != null) {
                    requireActivity().startActivityForResult(pickImageIntent, PICK_IMAGE);
                } else {
                    Toast.makeText(requireActivity(), requireActivity().getString(R.string.no_gallery_app_found), Toast.LENGTH_LONG).show();
                    Log.w(TAG, "User's device does not have gallery app");
                }
                dismiss();
            }
        });

        return builder.create();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE) {
            if (resultCode == RESULT_OK && data != null && data.getData() != null) {
                imageUriHandler.handleImageUri(data.getData());
            } else {
                Log.i(TAG, "PICK_IMAGE cancelled");
            }
        } else if (requestCode == TAKE_PICTURE) {
            if (resultCode == RESULT_OK) {
                if (tempPhotoFile == null) {
                    Log.e(TAG, "tempPhotoFile == null in TAKE_PICTURE onActivityResult");
                    return;
                }
                Uri tempPhotoFileUri = Uri.parse(tempPhotoFile.toURI().toString());
                imageUriHandler.handleImageUri(tempPhotoFileUri);
            } else {
                Log.i(TAG, "TAKE_PICTURE cancelled");
            }
        }
    }
}

package com.kcbs.webforum.Fragment;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.kcbs.webforum.R;
import com.kcbs.webforum.event.BanEvent;

import org.greenrobot.eventbus.EventBus;


public class PublisherDialogFragment extends DialogFragment {
    private EditText et_time,et_msg;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = getActivity().getLayoutInflater().inflate(R.layout.fragment_dialog, null);
        builder.setView(view);
        et_time = view.findViewById(R.id.et_time);
        et_msg = view.findViewById(R.id.et_msg);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        view.findViewById(R.id.tv_submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et_time.getText().toString().equals("")&&!et_msg.getText().toString().equals("")){
                    EventBus.getDefault().post(new BanEvent(Integer.parseInt(et_time.getText().toString()),et_msg.getText().toString()));
                    dialog.dismiss();
                }
            }
        });
        view.findViewById(R.id.tv_cancel).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
//        final String[] items = {"onSuccess", "onFailure"};
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                switch (which) {
//                    case 0:
//                        //success
//                        EventBus.getDefault().post(new onSuccessEvent());
//                        break;
//                    case 1:
//                        //failure
//                        EventBus.getDefault().post(new onFailureEvent("???"));
//                        break;
//                }
//           }
//       });
        return dialog;

    }


}

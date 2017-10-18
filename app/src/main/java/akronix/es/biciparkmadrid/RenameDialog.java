package akronix.es.biciparkmadrid;


import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class RenameDialog extends DialogFragment {

    RenameDialogCallback mRenameCallback;

    public static final String FAVOURITE_ID_BUNDLE_KEY = "favouriteId";
    public static final String NAME_BUNDLE_KEY = "name";

    /**
     * Create a new instance of MyDialogFragment, providing "num"
     * as an argument.
     */
    public static RenameDialog newInstance(long id, String name) {
        RenameDialog f = new RenameDialog();

        // Supply num input as an argument.
        Bundle args = new Bundle();
        args.putLong(FAVOURITE_ID_BUNDLE_KEY, id);
        args.putString(NAME_BUNDLE_KEY, name);
        f.setArguments(args);

        return f;
    }


    public interface RenameDialogCallback {
        public void doRename(long id, String newName);
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mRenameCallback = (RenameDialogCallback) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        final long favouriteId = bundle.getLong(FAVOURITE_ID_BUNDLE_KEY);
        final String oldName = bundle.getString(NAME_BUNDLE_KEY);

        View view = inflater.inflate(R.layout.fragment_rename_dialog, null);
        final EditText etName = (EditText) view.findViewById(R.id.etRenameDialogName);
        etName.setHint(oldName);

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton(R.string.rename, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String newName = etName.getText().toString();
                        if (newName != null && !newName.isEmpty())
                            mRenameCallback.doRename(favouriteId, newName);
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getActivity(), "Cancelled renaming", Toast.LENGTH_SHORT).show();
                        return;
                    }
                });
        return builder.create();
    }

    public RenameDialog() {
        // Required empty public constructor
    }

}

package edu.fsu.cs.mobile.outdoorsmanapp;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class RecordListAdapter extends RecyclerView.Adapter<RecordListAdapter.ViewHolder> {

    private ArrayList<HarvestRecord> mDataset;
    private Context mContext;

    private RecordListFragment.OnItemClick mListener;

    public void setOnItemClickListener(RecordListFragment.OnItemClick listener) {
        mListener = listener;
    }

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    class ViewHolder extends RecyclerView.ViewHolder {

        ViewHolder(View view) {
            super(view);

        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    RecordListAdapter(Context context, ArrayList<HarvestRecord> myDataset) {
        mDataset = myDataset;
        mContext = context;
    }

    // Create new views (invoked by the layout manager)
    @NonNull
    @Override
    public RecordListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.record_list_model, parent, false);
        // set the view's size, margins, paddings and layout parameters

        return new ViewHolder(v);
    }


    // Replace the contents of a view (invoked by the layout manager)
    
    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, @SuppressLint("RecyclerView") final int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        final HarvestRecord model = getItem(position);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                mListener.onItemClicked(v, position, model);
            }
        });
        TextView textViewId = holder.itemView.findViewById(R.id.textViewId);
        textViewId.setText(mContext.getString(R.string.harvestID)+model.getId());
        TextView textViewTypeId = holder.itemView.findViewById(R.id.textViewTypeId);
        textViewTypeId.setText(mContext.getString(R.string.typeID)+model.getTypeId());
        TextView textViewType = holder.itemView.findViewById(R.id.textViewType);
        textViewType.setText(mContext.getString(R.string.type)+model.getType());
        TextView textViewDate = holder.itemView.findViewById(R.id.textViewDate);

        textViewDate.setText(""+model.getDateString());
        TextView textViewLocation = holder.itemView.findViewById(R.id.textViewLocation);
        textViewLocation.setText(mContext.getString(R.string.locColonLat)+model.getLatLng().latitude+ " lng: " + ""+model.getLatLng().longitude);

    }

    private HarvestRecord getItem(int position) {
        return mDataset.get(position);
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }
}
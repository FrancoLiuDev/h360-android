package com.leedian.klozr.view.Adapter;
import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.blankj.utilcode.utils.NetworkUtils;
import com.leedian.klozr.R;
import com.leedian.klozr.model.dataOut.OviewListModel;
import com.leedian.klozr.model.restapi.UrlMessagesConstants;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import java.util.List;

/**
 * ContentListAdapter
 *
 * @author Franco
 */
public class ContentListAdapter
        extends RecyclerView.Adapter<ContentListAdapter.ContentItemView>
        implements Transformation
{

    private List<OviewListModel> items = null;

    private Transformation transformation = this;

    private Context context;

    private ContentListItemClickListener listener;

    public ContentListAdapter(Context context, List<OviewListModel> items) {

        this.context = context;
        this.items = items;
    }

    public void setContentListDataAndNotifyView(List<OviewListModel> items) {

        this.items = items;
        notifyDataSetChanged();
    }

    public void setContentListItemListener(ContentListItemClickListener listener) {

        this.listener = listener;
    }

    @Override public ContentItemView onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext())
                                        .inflate(R.layout.adapter_content_list_item, parent, false);

        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutView.setLayoutParams(lp);

        return new ContentItemView(layoutView);
    }

    @Override public void onBindViewHolder(final ContentItemView holder, int position) {

        String         img_path;
        OviewListModel listModel = this.items.get(position);

        img_path = UrlMessagesConstants.getFileDownloadRestUrl(listModel.getThumbkey());

        if (NetworkUtils.isConnected(context)) {
            Picasso.with(context).load(img_path).transform(transformation)
                   .into(holder.imageView, new LoadingCallback(holder));
        } else {
            Picasso.with(context).load(img_path).networkPolicy(NetworkPolicy.OFFLINE)
                   .transform(transformation).into(holder.imageView, new LoadingCallback(holder));
        }

        holder.textView.setText(listModel.getName());
    }

    @Override public int getItemCount() {

        if (this.items == null) {
            return 0;
        }
        return this.items.size();
    }

    @Override public Bitmap transform(Bitmap source) {

        double aspectRatio  = (double) source.getHeight() / (double) source.getWidth();
        int    NMega        = 1024 * 1024 * 2;
        double sourceWidth  = source.getWidth();
        double sourceHeight = source.getHeight();
        double tranWidth    = sourceWidth;
        double tranHeight   = sourceHeight;

        if ((sourceWidth * sourceHeight) > (NMega)) {
            tranHeight = Math.sqrt(NMega * aspectRatio);
            tranWidth = Math.sqrt(NMega / aspectRatio);
        }

        Bitmap result = Bitmap.createScaledBitmap(source, (int) tranWidth, (int) tranHeight, false);

        if (result != source) {
            source.recycle();
        }

        return result;
    }

    @Override public String key() {

        return "";
    }

    public interface ContentListItemClickListener {
        void onRowClicked(int position);

        void onViewClicked(View v, int position);

        void onViewDeleteItemClicked(View v, int position);
    }

    private class LoadingCallback
            implements Callback
    {
        ContentItemView holder;

        LoadingCallback(ContentItemView holder) {

            this.holder = holder;
        }

        @Override public void onSuccess() {

        }

        @Override public void onError() {

        }
    }

    class ContentItemView
            extends RecyclerView.ViewHolder
    {
        ImageView imageView;

        TextView textView;

        ImageView delete_imageView;

        View.OnClickListener onItemClicked = new View.OnClickListener() {
            @Override public void onClick(View v) {

                if (listener != null) {
                    listener.onRowClicked(getAdapterPosition());
                }
            }
        };

        View.OnClickListener onItemImageClicked = new View.OnClickListener() {
            @Override public void onClick(View v) {

                if (listener != null) {
                    listener.onViewClicked(v, getAdapterPosition());
                }
            }
        };

        View.OnClickListener onItemDeleteImageClicked = new View.OnClickListener() {
            @Override public void onClick(View v) {

                if (listener != null) {
                    listener.onViewDeleteItemClicked(v, getAdapterPosition());
                }
            }
        };

        ContentItemView(View itemView) {

            super(itemView);
            imageView = (ImageView) itemView.findViewById(R.id.img);
            textView = (TextView) itemView.findViewById(R.id.img_name);
            delete_imageView = (ImageView) itemView.findViewById(R.id.delete_image);
            itemView.setOnClickListener(onItemClicked);
            imageView.setOnClickListener(onItemImageClicked);
            delete_imageView.setOnClickListener(onItemDeleteImageClicked);
        }
    }
}

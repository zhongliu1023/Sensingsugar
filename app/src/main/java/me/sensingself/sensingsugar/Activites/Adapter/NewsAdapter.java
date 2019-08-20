package me.sensingself.sensingsugar.Activites.Adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import me.sensingself.sensingsugar.Lib.FontUtility;
import me.sensingself.sensingsugar.Model.News;
import me.sensingself.sensingsugar.R;

import java.util.ArrayList;

/**
 * Created by liujie on 1/12/18.
 */

public class NewsAdapter extends ArrayAdapter<News> {

    int resource;
    private Context context;
    private ArrayList<News> datas;


    public NewsAdapter(Context _context, int _resource, ArrayList<News>  _items) {
        super(_context, _resource, _items);
        resource = _resource;
        context = _context;
        datas = _items;
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        Log.d("ChatAdapter", "getView function , dataset changed");

        final News data = datas.get(position);
        if (data == null)
            return null;

        ViewHolder holder;
        view = LayoutInflater.from(context).inflate(R.layout.adapter_news, null);

        holder = new ViewHolder();
        holder.newsImageView = (ImageView) view.findViewById(R.id.newsImageView);
        holder.newsContent = (TextView)view.findViewById(R.id.newsContent);
        holder.newsUrls = (TextView)view.findViewById(R.id.newsUrl);
        holder.newsImageView.setImageResource(R.drawable.sample_news);
        holder.newsContent.setTypeface(FontUtility.getOfficinaSansCBold(view.getContext()));
        holder.newsUrls.setTypeface(FontUtility.getOfficinaSansCBook(view.getContext()));
        view.setTag(holder);
        holder = (ViewHolder) view.getTag();
        holder.initHolder(data);

        return view;
    }

    private class ViewHolder {
        ImageView newsImageView;
        TextView newsContent;
        TextView newsUrls;


        public void initHolder(final News data) {
//            String photoLink = "https://graph.facebook.com/" + data.getUser_facebookid() + "/picture?type=large";
//            String fullName = data.getUser_firstname() + " " + data.getUser_lastname();
//            Picasso.with(context)
//                    .load(photoLink)
//                    .placeholder(R.drawable.no_image)
//                    .error(R.drawable.no_image)
//                    .into(ivPhoto);
//            //ImageLoader.getInstance().displayImage(data.getPhotoUrl(), ivPhoto, HopSpottrApplication.img_opt_photo);
//            tvName.setText(fullName);
        }
    }

}
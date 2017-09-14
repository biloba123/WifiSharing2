package frame.tool;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.List;


/**
 *　　┏┓　　  ┏┓+ +
 *　┏┛┻━ ━ ━┛┻┓ + +
 *　┃　　　　　　  ┃
 *　┃　　　━　　    ┃ ++ + + +
 *     ████━████     ┃+
 *　┃　　　　　　  ┃ +
 *　┃　　　┻　　  ┃
 *　┃　　　　　　  ┃ + +
 *　┗━┓　　　┏━┛
 *　　　┃　　　┃　　　　　　　　　　　
 *　　　┃　　　┃ + + + +
 *　　　┃　　　┃
 *　　　┃　　　┃ +  神兽保佑
 *　　　┃　　　┃    代码无bug！　
 *　　　┃　　　┃　　+　　　　　　　　　
 *　　　┃　 　　┗━━━┓ + +
 *　　　┃ 　　　　　　　┣┓
 *　　　┃ 　　　　　　　┏┛
 *　　　┗┓┓┏━┳┓┏┛ + + + +
 *　　　　┃┫┫　┃┫┫
 *　　　　┗┻┛　┗┻┛+ + + +
 * ━━━━━━神兽出没━━━━━━
 * Author：LvQingYang
 * Date：2017/3/15
 * Email：biloba12345@gamil.com
 * Info：RecyclerView万能适配器
 */



public abstract class SolidRVBaseAdapter<T> extends RecyclerView.Adapter<SolidRVBaseAdapter.SolidCommonViewHolder> {
    protected List<T> mBeans;
    protected Context mContext;

    public SolidRVBaseAdapter(Context context, List<T> beans) {
        mContext = context;
        mBeans = beans;
    }

    @Override
    public SolidRVBaseAdapter.SolidCommonViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        View view = inflater.inflate(getItemLayoutID(viewType), parent, false);
        SolidCommonViewHolder holder = new SolidCommonViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(SolidRVBaseAdapter.SolidCommonViewHolder holder, int position) {
        if (mBeans.size()>position) {
            onBindDataToView(holder, mBeans.get(position));
        }
    }

    /*
    绑定数据到Item的控件中去
    @param holder
    @param bean
    */
    protected abstract void onBindDataToView(SolidCommonViewHolder holder, T bean);

    /*
      取得ItemView的布局文件
      @return
     */
    public abstract int getItemLayoutID(int viewType);

    @Override
    public int getItemCount() {
        return mBeans.size();
    }

    /**
     * 添加
     */
    public void addItem(T bean) {
        mBeans.add(bean);
        notifyItemInserted(mBeans.size() - 1);
    }

    public void addItem(T bean, int pos) {
        mBeans.add(pos, bean);
        notifyItemInserted(pos);
    }

    public void addItems(List<T> beans) {
        mBeans.addAll(beans);
        notifyItemRangeInserted(mBeans.size() - beans.size(), beans.size());
    }

    public void addItems(List<T> beans, int pos) {
        mBeans.addAll(pos, beans);
        notifyItemRangeInserted(pos, beans.size());
    }

    /**
     * 移除
     */
    public void removeItem(T bean) {
        removeItem(mBeans.indexOf(bean));
    }

    public void removeItem(int pos) {
        mBeans.remove(pos);
        notifyItemRemoved(pos);
    }


    public void clearAllItems() {
        mBeans.clear();
        notifyDataSetChanged();
    }

    /**
     * 更新
     */
    public void updateItem(int pos){
        notifyItemChanged(pos);
    }

    public void updateItem(int pos,T bean){
        mBeans.remove(pos);
        mBeans.add(pos,bean);
        notifyItemChanged(pos);
    }

    public class SolidCommonViewHolder extends RecyclerView.ViewHolder {
        private final SparseArray<View> mViews;
        private View itemView;

        public SolidCommonViewHolder(View itemView) {
            super(itemView);
            this.mViews = new SparseArray<>();
            this.itemView = itemView;
            //添加Item的点击事件
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos=getAdapterPosition();
                    if (mBeans.size()>pos) {
                        onItemClick(pos,mBeans.get(pos));
                    }
                }
            });
            //添加Item的长按事件
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int pos=getAdapterPosition();
                    if (mBeans.size()>pos) {
                        return onItemLongClick(pos, mBeans.get(pos));
                    }
                    return false;
                }
            });
        }

        public <T extends View> T getView(int viewId) {
            View view = mViews.get(viewId);
            if (view == null) {
                view = itemView.findViewById(viewId);
                mViews.put(viewId, view);
            }
            return (T) view;
        }

        public void setText(int viewId, String text) {
            TextView tv = getView(viewId);
            tv.setText(text);
        }

        /**
        加载drawable中的图片

        @param viewId
        @param resId
        */
        public void setImage(int viewId, int resId) {
            ImageView iv = getView(viewId);
            Glide.with(mContext)
                    .load(resId)
                    .into(iv);
            iv.setImageResource(resId);
        }

        public void setImage(int viewId, Drawable drawable) {
            ImageView iv = getView(viewId);
            iv.setImageDrawable(drawable);
        }

        /**
          加载网络上的图片

          @param viewId
          @param url
         */
        public void setImageFromInternet(int viewId, String url) {
            ImageView iv = getView(viewId);
            Glide.with(mContext)
                    .load(iv)
                    .into(iv);
        }
    }

    protected void onItemClick(int position,T bean) {
    }

    protected boolean onItemLongClick(int position,T bean) {
        return false;
    }
}


package com.tmvlg.factorcapgame.ui.multiplayergame.lobby.find

import android.view.ViewGroup
import com.tmvlg.factorcapgame.data.repository.firebase.Lobby
import com.tmvlg.factorcapgame.databinding.LobbyBinding
import mva3.adapter.ItemBinder
import mva3.adapter.ItemViewHolder

class LobbyBinder : ItemBinder<Lobby, LobbyBinder.ViewHolder>() {
    override fun bindViewHolder(holder: ViewHolder?, item: Lobby?) {
        TODO("Not yet implemented")
    }

    override fun createViewHolder(parent: ViewGroup?): ViewHolder {
        TODO("Not yet implemented")
    }

    override fun canBindData(item: Any?): Boolean {
        TODO("Not yet implemented")
    }

    class ViewHolder(
        val binding: LobbyBinding
    ) : ItemViewHolder<Lobby>(binding.root)

}
//
//// ItemBinder for NewsSource
//public class NewsSourceBinder extends ItemBinder<NewsSource, NewsSourceBinder.ViewHolder> {
//
//    @Override public ViewHolder createViewHolder(ViewGroup parent) {
//        return new ViewHolder(inflate(parent, R.layout.item_news_source));
//    }
//
//    @Override public void bindViewHolder(ViewHolder holder, NewsSource item) {
//        holder.imageView.setImageResource(item.getIconResource());
//        holder.textView.setText(item.getText());
//        int bgColor = ContextCompat.getColor(holder.textView.getContext(),
//        holder.isItemSelected() ? item.getColor() : R.color.cardview_light_background);
//        holder.cardView.setBackgroundColor(bgColor);
//        holder.cardView.setCardElevation(holder.isItemSelected() ? 16 : 0);
//    }
//
//    @Override public boolean canBindData(Object item) {
//        return item instanceof NewsSource;
//    }
//
//    public static class ViewHolder extends ItemViewHolder<NewsSource> {
//
//        private ImageView imageView;
//        private TextView textView;
//        private CardView cardView;
//
//        public ViewHolder(View itemView) {
//            super(itemView);
//
//            imageView = itemView.findViewById(R.id.image_view);
//            cardView = (CardView) itemView;
//            textView = itemView.findViewById(R.id.text_view);
//        }
//    }
//}
//
//// ItemBinder for Header
//public class HeaderBinder extends ItemBinder<String, HeaderBinder.ViewHolder> {
//
//    @Override public ViewHolder createViewHolder(ViewGroup parent) {
//        return new ViewHolder(inflate(parent, R.layout.item_header));
//    }
//
//    @Override public void bindViewHolder(ViewHolder holder, String item) {
//        holder.header.setText(item);
//    }
//
//    @Override public boolean canBindData(Object item) {
//        return item instanceof String;
//    }
//
//    static class ViewHolder extends ItemViewHolder<String> {
//
//        private TextView header;
//
//        ViewHolder(View itemView) {
//            super(itemView);
//            header = itemView.findViewById(R.id.tv_header);
//        }
//    }
//}

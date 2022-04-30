package id.semisamadriver.app.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import id.semisamadriver.app.utilily.EndlessRecyclerViewScrollListener


@SuppressLint("NotifyDataSetChanged")
// Custom Adapter
class Adapter<T>(var layout: Int,
                 private var items: MutableList<T>,
                 var view:(View, T) -> Unit,
                 var handler:(Int, T) -> Unit): RecyclerView.Adapter<Adapter.ViewHolder<T>>() {


    private var recyclerView: RecyclerView? = null
    var data = this.items
        set(value) {
            field = value.filterNotNull().toMutableList()
            notifyDataSetChanged()
        }

    fun addAll(items: List<T>) {
        this.data.addAll(items)
        notifyDataSetChanged()
    }

    fun deletePosition(position: Int) {
        this.data.removeAt(position)
        notifyDataSetChanged()
    }

    fun clearScrollListener(scrollListener: EndlessRecyclerViewScrollListener) {
        this.data.clear()
        notifyDataSetChanged()
        recyclerView?.clearOnScrollListeners()
        recyclerView?.addOnScrollListener(scrollListener)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        this.recyclerView = null
    }

    fun refresh() { notifyDataSetChanged() }

    fun refreshItem(position: Int) {
        notifyItemChanged(position)
    }

    fun refreshItem(item: T) {
        val index = data.indexOf(item)
        if (index > -1)
            notifyItemChanged(index)
    }

    class ViewHolder<T>(itemView: View): RecyclerView.ViewHolder(itemView) {
        fun bind(item: T, view: (View, T) -> Unit) {
            view(itemView, item)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<T> {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(this.layout, parent, false))
    }

    override fun getItemCount(): Int {
        return this.data.size
    }

    override fun onBindViewHolder(holder: ViewHolder<T>, position: Int) {
        val item: T = this.data[position]
        holder.bind(item, view)
        holder.itemView.setOnClickListener { handler(position, item) }
    }

}
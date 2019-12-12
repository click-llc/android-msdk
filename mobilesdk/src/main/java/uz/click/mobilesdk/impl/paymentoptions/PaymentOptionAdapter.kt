package uz.click.mobilesdk.impl.paymentoptions

import android.content.Context
import android.support.v4.content.ContextCompat
import android.support.v7.view.ContextThemeWrapper
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import kotlinx.android.synthetic.main.item_payment_option.view.*
import uz.click.mobilesdk.R

/**
 * @author rahmatkhujaevs on 29/01/19
 * */
class PaymentOptionAdapter(
    val context: Context,
    val themeMode: ThemeOptions = ThemeOptions.LIGHT,
    val items: ArrayList<PaymentOption>
) :
    RecyclerView.Adapter<PaymentOptionAdapter.PaymentOptionViewHolder>() {

    lateinit var callback: OnPaymentOptionSelected

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PaymentOptionViewHolder {
        when (themeMode) {
            ThemeOptions.LIGHT -> {
                val contextWrapper = ContextThemeWrapper(parent.context, R.style.Theme_App_Light)

                return PaymentOptionViewHolder(
                    LayoutInflater.from(context).cloneInContext(contextWrapper).inflate(
                        R.layout.item_payment_option,
                        parent,
                        false
                    )
                )
            }
            ThemeOptions.NIGHT -> {
                val contextWrapper = ContextThemeWrapper(parent.context, R.style.Theme_App_Dark)

                return PaymentOptionViewHolder(
                    LayoutInflater.from(context).cloneInContext(contextWrapper).inflate(
                        R.layout.item_payment_option,
                        parent,
                        false
                    )
                )
            }
        }

    }

    override fun getItemCount(): Int = items.size

    override fun onBindViewHolder(holder: PaymentOptionViewHolder, position: Int) {
        holder.image.setImageDrawable(ContextCompat.getDrawable(context, items[position].image))
        holder.title.text = items[position].title
        holder.subtitle.text = items[position].subtitle

        holder.divider.visibility = if (position == items.size - 1) View.INVISIBLE else View.VISIBLE

        holder.itemView.setOnClickListener {
            callback.selected(position, items[position])
        }
    }

    class PaymentOptionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val image: ImageView = itemView.ivOptionImage
        val title: TextView = itemView.tvOptionTitle
        val subtitle: TextView = itemView.tvOptionSubtitle
        val divider: View = itemView.divider
    }

    interface OnPaymentOptionSelected {
        fun selected(position: Int, item: PaymentOption)
    }
}
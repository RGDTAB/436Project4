package com.raydid.dictionaryaggregator


import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray


class DefinitionAdapter(private val dataSet: JSONArray, private val filter: String) :
    RecyclerView.Adapter<DefinitionAdapter.ViewHolder>() {


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvPartOfSpeech: TextView = view.findViewById(R.id.tvPartOfSpeech)
        val tvDefinition: TextView = view.findViewById(R.id.tvDefinitions)
        val tvSynonyms: TextView = view.findViewById(R.id.tvSynonyms)
        val tvAntonyms: TextView = view.findViewById(R.id.tvAntonyms)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.definition_view, parent, false)
        return ViewHolder(view)
    }


    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val meaning = dataSet.getJSONObject(position)
        val partOfSpeech = meaning.optString("partOfSpeech", "N/A")


        // Display only if "All" is selected or if part of speech match
        if (filter == "All" || filter.equals(partOfSpeech, ignoreCase = true)) {
            holder.itemView.visibility = View.VISIBLE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )


            holder.tvPartOfSpeech.text = partOfSpeech


            // Parse Definitions
            val definitions = meaning.getJSONArray("definitions")
            var definitionText = ""
            for (i in 0 until definitions.length()) {
                val def = definitions.getJSONObject(i)
                definitionText += "${i + 1}. ${def.getString("definition")}\n\n"
            }
            holder.tvDefinition.text = definitionText


            // Parse Synonyms logic
            val synonyms = meaning.optJSONArray("synonyms")
            if (synonyms != null && synonyms.length() > 0) {
                holder.tvSynonyms.visibility = View.VISIBLE
                val synList = (0 until synonyms.length()).joinToString(", ") { synonyms.getString(it) }
                // Use string resource with placeholder to avoid lint warnings
                holder.tvSynonyms.text = holder.itemView.context.getString(R.string.synonyms_label, synList)
            } else {
                holder.tvSynonyms.visibility = View.GONE
            }


            // Parse Antonyms logic
            val antonyms = meaning.optJSONArray("antonyms")
            if (antonyms != null && antonyms.length() > 0) {
                holder.tvAntonyms.visibility = View.VISIBLE
                val antList = (0 until antonyms.length()).joinToString(", ") { antonyms.getString(it) }
                // Use string resource with placeholder to avoid lint warnings
                holder.tvAntonyms.text = holder.itemView.context.getString(R.string.antonyms_label, antList)
            } else {
                holder.tvAntonyms.visibility = View.GONE
            }


        } else {
            // Completely hides item if it doesn't match filter
            holder.itemView.visibility = View.GONE
            holder.itemView.layoutParams = RecyclerView.LayoutParams(0, 0)
        }
    }


    override fun getItemCount(): Int {
        return dataSet.length()
    }
}

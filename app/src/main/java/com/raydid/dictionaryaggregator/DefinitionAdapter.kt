package com.raydid.dictionaryaggregator

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.json.JSONArray

class DefinitionAdapter(private val dataSet : JSONArray) :
        RecyclerView.Adapter<DefinitionAdapter.ViewHolder>() {

    class ViewHolder(view : View) : RecyclerView.ViewHolder(view) {
        val tvPartOfSpeech : TextView
        val tvDefinition : TextView

        init {
            tvPartOfSpeech = view.findViewById<TextView>(R.id.tvPartOfSpeech)
            tvDefinition = view.findViewById<TextView>(R.id.tvDefinitions)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.definition_view, parent, false)

        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val meaning = dataSet.getJSONObject(position)
        holder.tvPartOfSpeech.text = meaning.getString("partOfSpeech")

        val definitions = meaning.getJSONArray("definitions")
        var definitionText = ""
        for (i in 0 until definitions.length()) {
            val def = definitions.getJSONObject(i)
            definitionText += def.getString("definition") + "\n\n"
        }
        holder.tvDefinition.text = definitionText
    }

    override fun getItemCount(): Int {
        return dataSet.length()
    }
}
package com.example.namnyam.ui.client

import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object CartStore {

    private const val PREFS_NAME = "cart_prefs"
    private const val KEY_CART = "cart_items"

    private val gson = Gson()

    fun save(context: Context, items: List<CartItemUi>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit()
            .putString(KEY_CART, gson.toJson(items))
            .apply()
    }

    fun load(context: Context): MutableList<CartItemUi> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_CART, null) ?: return mutableListOf()

        val type = object : TypeToken<MutableList<CartItemUi>>() {}.type
        return gson.fromJson(json, type) ?: mutableListOf()
    }

    fun clear(context: Context) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().remove(KEY_CART).apply()
    }
}
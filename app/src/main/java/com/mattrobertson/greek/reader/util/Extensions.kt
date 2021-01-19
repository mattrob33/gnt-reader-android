package com.mattrobertson.greek.reader.util

import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.mattrobertson.greek.reader.R

fun Fragment.getNavigationResult(key: String = "result") = requireActivity().findNavController(R.id.core_nav_host_fragment).currentBackStackEntry?.savedStateHandle?.getLiveData<Int>(key)

fun Fragment.setNavigationResult(result: Int, key: String) = requireActivity().findNavController(R.id.core_nav_host_fragment).currentBackStackEntry?.savedStateHandle?.set(key, result)
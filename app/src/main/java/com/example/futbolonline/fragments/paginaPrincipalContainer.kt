package com.example.futbolonline.fragments

import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProvider
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.widget.ViewPager2
import com.example.futbolonline.R
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator

class paginaPrincipalContainer : Fragment() {

    companion object {
        fun newInstance() = paginaPrincipalContainer()
    }

    private lateinit var viewModel: PaginaPrincipalContainerViewModel
    lateinit var v: View
    lateinit var viewPagerPaginaPrincipalContainer: ViewPager2
    lateinit var tableLayoutPaginaPrincipalContainer: TabLayout

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        v = inflater.inflate(R.layout.pagina_principal_container_fragment, container, false)
        viewPagerPaginaPrincipalContainer = v.findViewById(R.id.viewPagerPaginaPrincipalContainer)
        tableLayoutPaginaPrincipalContainer = v.findViewById(R.id.tableLayoutPaginaPrincipalContainer)
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProvider(this).get(PaginaPrincipalContainerViewModel::class.java)
        // TODO: Use the ViewModel
    }

    override fun onStart() {
        super.onStart()

        viewPagerPaginaPrincipalContainer.setAdapter(createCardAdapter())
        // viewPager.isUserInputEnabled = false
        TabLayoutMediator(tableLayoutPaginaPrincipalContainer, viewPagerPaginaPrincipalContainer) { tab, position ->
            when (position) {
                0 -> tab.text = "Buscar partidos"
                1 -> tab.text = "Proximos partidos"
                2 -> tab.text = "Historial partidos"
                3 -> tab.text = "Mi perfil"
                else -> tab.text = "undefined"
            }
        }.attach()
    }

    private fun createCardAdapter(): ViewPagerAdapter? {
        return ViewPagerAdapter(requireActivity())
    }

    class ViewPagerAdapter(fragmentActivity: FragmentActivity) : FragmentStateAdapter(fragmentActivity) {
        override fun createFragment(position: Int): Fragment {

            return when(position){
                0 -> tabBuscarPartidos()
                1 -> tabProximosPartidos()
                2 -> tabHistorialPartidos()
                3 -> tabMiPerfil()
                else -> login()
            }
        }

        override fun getItemCount(): Int {
            return TAB_COUNT
        }

        companion object {
            private const val TAB_COUNT = 4
        }
    }

}
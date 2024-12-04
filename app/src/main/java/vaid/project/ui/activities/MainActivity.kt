package vaid.project.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.google.android.gms.location.LocationServices
import com.google.android.material.bottomnavigation.BottomNavigationView
import vaid.project.R
import vaid.project.database.remote.AppwriteAPI
import vaid.project.location.DefaultLocationClient
import vaid.project.repository.Repository
import vaid.project.viewmodels.LocationViewModel
import vaid.project.viewmodels.LocationViewModelFactory

class MainActivity : AppCompatActivity() {

    lateinit var viewModel: LocationViewModel
    private lateinit var bottomNavigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val repository = Repository()
        val viewModelProviderFactory  =  LocationViewModelFactory(
            locationClient = DefaultLocationClient(applicationContext, LocationServices.getFusedLocationProviderClient(this)),
            repository = repository)
        viewModel = ViewModelProvider(this, viewModelProviderFactory)[LocationViewModel::class.java]

        setContentView(R.layout.activity_main)

        bottomNavigationView = findViewById(R.id.bottomNavigationView)
        val navigationController = findNavController(R.id.fragmentContainerView)
        bottomNavigationView.setupWithNavController(navigationController)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    fun setVisibilityForBottomNavigationView(visibility: Int){
        bottomNavigationView.visibility = visibility
    }

}
package vaid.project.ui.fragments

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context.LOCATION_SERVICE
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import kotlinx.coroutines.launch
import vaid.project.R
import vaid.project.location.LocationService
import vaid.project.model.User
import vaid.project.ui.activities.MainActivity
import vaid.project.utils.Result
import vaid.project.utils.SessionUtil
import vaid.project.viewmodels.LocationViewModel


class MapFragment : BaseFragment(), OnMapReadyCallback {

    override var bottomNavigationVisibility: Int = View.VISIBLE

    private var map: GoogleMap? = null
    private var mapView: MapView? = null
    private var locationProviderClient: FusedLocationProviderClient? = null
    private var currentMarker: Marker? = null
    private var toolbar: Toolbar? = null
    private var spinner: Spinner? = null

    private var viewModel: LocationViewModel? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_map, container, false)

        viewModel = (activity as MainActivity).viewModel
        toolbar = view.findViewById(R.id.toolbar)
        spinner =  view.findViewById(R.id.group_spinner)
        viewModel?.getAllGroups(SessionUtil(requireContext()).getPreference("userId"))

        isGPSEnable()

        if (ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION), 0)
        } else {

            startLocationService()
            locationProviderClient =
                LocationServices.getFusedLocationProviderClient(requireContext())

            mapView = view.findViewById(R.id.mapView)
            mapView?.onCreate(savedInstanceState)
            mapView?.getMapAsync(this)
        }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val options = arrayListOf<String>()

        viewModel?.parentItems?.observe(viewLifecycleOwner) { groups ->

            options.clear()
            for (group in groups) {
                options.add(group.title)
            }

            val adapter = ArrayAdapter(requireContext(), R.layout.spinner_item, options)
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            spinner?.adapter = adapter

            spinner?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                    val selectedItem = parent.getItemAtPosition(position).toString()
                    lifecycleScope.launch {
                        try {
                            val groupUsers = viewModel?.getGroupUsers(
                                selectedItem,
                                SessionUtil(requireContext()).getPreference("userId")
                            )?.await()

                            val currentMarkerPosition = currentMarker?.position

                            map?.clear()

                            groupUsers?.forEach { user ->
                                addMarkerForUser (user, user.latitude, user.longitude)
                            }

                            currentMarkerPosition?.let { position ->
                                currentMarker = map?.addMarker(MarkerOptions().position(position))
                            }
                        }catch (e: Exception){
                            Toast.makeText(requireContext(), "Нет пользователей в данной группе", Toast.LENGTH_SHORT).show()
                        }
                    }
                }

                override fun onNothingSelected(parent: AdapterView<*>) {

                }
            }
        }
    }

    private fun addMarkerForUser (user: User, latitude: Double, longitude: Double) {
        val latLng = LatLng(latitude, longitude)
        val markerOptions = MarkerOptions()
            .position(latLng)
            .title(user.name)
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_CYAN))
        map?.addMarker(markerOptions)
    }

    private fun startLocationService() {
        val service = Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_START
        }
        activity?.startService(service)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        map?.isMyLocationEnabled = true
        map?.uiSettings?.isZoomControlsEnabled = true

        viewModel?.location?.observe(viewLifecycleOwner) { result ->
            when (result) {
                is Result.Success -> {
                    updateMapLocation(result.data)

                    val session = SessionUtil(requireContext()).getPreference("userId")
                    if (session.isNotEmpty())
                        viewModel?.updateUserLocation(
                            session, User(name = null,  latitude = result.data.latitude, longitude = result.data.longitude, groupsIDs = null)
                        )
                }
                is Result.Error -> {
                    Toast.makeText(context, result.exception.error, Toast.LENGTH_SHORT).show()
                    Log.d("myLog", "onMapReady: ${result.exception.error}")
                }
            }
        }
    }

    private fun isGPSEnable() {
        val service = requireContext().getSystemService(LOCATION_SERVICE) as LocationManager?
        val enabled = service?.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if (!enabled!!) {
            val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
            startActivity(intent)
        }
    }

    private fun updateMapLocation(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        if (currentMarker == null) {
            currentMarker = map?.addMarker(MarkerOptions().position(latLng))
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
        } else {
            currentMarker?.position = latLng
            map?.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
            map?.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        }
    }

    private fun stopLocationService() {
        Intent(context, LocationService::class.java).apply {
            action = LocationService.ACTION_STOP
            activity?.startService(this)
        }
    }

    override fun onStart() {
        super.onStart()
        mapView?.onStart()
    }

    override fun onResume() {
        super.onResume()
        mapView?.onResume()
    }

    override fun onStop() {
        super.onStop()
        mapView?.onStop()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView?.onDestroy()
        stopLocationService()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapView?.onLowMemory()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mapView?.onSaveInstanceState(outState)
    }
}

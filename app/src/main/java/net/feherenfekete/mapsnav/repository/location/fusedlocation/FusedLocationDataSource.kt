package net.feherenfekete.mapsnav.repository.location.fusedlocation

import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import net.feherenfekete.mapsnav.model.LatLongData
import net.feherenfekete.mapsnav.repository.location.LocationDataSource
import java.util.concurrent.Executors
import javax.inject.Inject

class FusedLocationDataSource @Inject constructor(
    val fusedLocationProvider: FusedLocationProviderClient
) : LocationDataSource {

    override fun location(): Single<LatLongData> {
        val executor = Executors.newFixedThreadPool(1)
        return Single.create<LatLongData> { emitter ->
            fusedLocationProvider.lastLocation.addOnCompleteListener(
                executor,
                object : OnCompleteListener<Location> {
                    override fun onComplete(task: Task<Location>) {
                        if (task.isSuccessful && task.result != null) {
                            val location = task.result!!
                            emitter.onSuccess(
                                LatLongData(
                                    location.latitude,
                                    location.longitude
                                )
                            )
                        } else {
                            emitter.onError(RuntimeException("Cannot get fused location"))
                        }
                    }
                })
        }.subscribeOn(Schedulers.from(executor))
    }

}

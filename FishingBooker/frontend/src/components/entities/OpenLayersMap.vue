<template>
    <div class="content">
        <div class="map-style">
            <div id="map"></div>
        </div><br/>
        <div class="fields">
            <input id="streetID"        type="text"   class="form-control"  v-model="address.streetName"    @change="changeAddress()"  placeholder="Street name*">  <br/>
            <input id="streetNumID"     type="text"   class="form-control"  v-model="address.streetNumber"  @change="changeAddress()"  placeholder="Street number*"><br/>
            <input id="postalcodeID"    type="text"   class="form-control"  v-model="address.postalCode"    @change="changeaddress()"  placeholder="Postalcode*">   <br/>
            <input id="cityID" 	        type="text"   class="form-control"  v-model="address.city"          @change="changeAddress()"  placeholder="City*">         <br/>
            <input id="countryID" 	    type="text"   class="form-control"  v-model="address.country"       @change="changeAddress()"  placeholder="Country*">      <br/>
            <input id="latitudeID"      type="number" class="form-control"  v-model="address.latitude"      @change="changeAddress()"  placeholder="Latitude*">     <br/>
            <input id="longitudeID"     type="number" class="form-control"  v-model="address.longitude"     @change="changeAddress()"  placeholder="Longitude*">    <br/>
        </div>  
    </div>
</template>

<script>
import View from 'ol/View'
import Map from 'ol/Map'
import TileLayer from 'ol/layer/Tile' 
import OSM from 'ol/source/OSM'
import {toLonLat} from 'ol/proj';
import {fromLonLat} from 'ol/proj';
import 'ol/ol.css'
import useValidate from '@vuelidate/core'

export default {
    props:['location'],
    setup() {
            return {v$: useValidate()}
    },
    data() {
            return{    
                address: {     
                    streetName: '',
                    streetNumber: '',
                    postalcode: '',
                    city: '',
                    country: '',
                    longitude: '',
                    latitude: ''  
                }       
            }
    },
    mounted() {
      var center = fromLonLat([19.41, 44.82]);

        if(this.existedAddress)
            this.address = this.existedAddress;

        var mapSearch = new Map({
        target: 'map',
        layers: [
          // adding a background tiled layer
          new TileLayer({
          source: new OSM()    
        }),
        ],
		view : new View({
		center:center,
		zoom: 7,
	}),
      })

        mapSearch.on('click', function (evt) {          
            var coord = toLonLat(evt.coordinate);
            reverseGeocode(coord);
            this.$emit('change-address', this.address);
      })
    },
    methods: {
        changeAddress(){
            this.$emit('change-address', this.address)
        }
    }
}
    setTimeout(() => { this.map.updateSize(); });

    function reverseGeocode (coords) {
        fetch('http://nominatim.openstreetmap.org/reverse?format=json&lon=' + coords[0] + '&lat=' + coords[1])
            .then(function (response) {
                return response.json();
            }).then(function (json) {
            
            //https://stackoverflow.com/questions/56348513/how-to-change-v-model-value-from-js
        
            let elem = document.getElementById("longitudeID");
            elem.value = coords[0].toFixed(2);
            elem.dispatchEvent(new Event('input'));
            
            let el = document.getElementById("latitudeID");
                el.value = coords[1].toFixed(2);
                el.dispatchEvent(new Event('input'));
                
            if (json.address.road) {
                let el = document.getElementById("streetID");
                el.value = json.address.road;
                el.dispatchEvent(new Event('input'));
            } 

            if(json.address.streetNumber){
                let el = document.getElementById("streetNumID");
                el.value = json.address.streetNumber;
                el.dispatchEvent(new Event('input'));
            }

            if (json.address.city) {
                let el = document.getElementById("cityID");
                el.value = json.address.city;
                el.dispatchEvent(new Event('input'));
             } else if (json.address.city_district) {
                let el = document.getElementById("cityID");
                el.value = json.address.city_district;
                el.dispatchEvent(new Event('input'));
            }
                
            if (json.address.postcode) {
                let el = document.getElementById("postalcodeID");
                el.value = json.address.postcode;
                el.dispatchEvent(new Event('input'));
            } 
                
            if (json.address.country) {
                let el = document.getElementById("countryID");
                el.value = json.address.country;
                el.dispatchEvent(new Event('input'));
             } 
          });

           const util = require('util')
           console.log(util.inspect(this.address, false, null, true)) 
        }
    
</script>

<style scoped>
    .content{
        display:flex;
        flex-direction: column;
    }
    .map-style{ 
        width: 500px;
        height: 400px;
    }
    #map {
        width: 100%;
        height: 100%;
    }
    .fields{
        width: 600px;
        margin-left: 10%;
    }
    .form-control{
        border-radius:1.5rem;
        width: 400px;
        height: 40px;  
    }
</style>
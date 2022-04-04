Date 4.4.2022  
App: CurrentWeather get recent air temperature  

Tools: Bumblebee 2021.1.1    
Library: Volley 1.2.1  


Harjoitus 18 – Säätila nyt
Hyvin samankaltainen kuin edellä, mutta käsitellään Ilmatieteenlaitoksen Opendata.fmi.fi –
palvelun tarjoamaa tietoa.
• Käyttäjä voi valita/kirjoittaa halutun sijainnin (Esim spinner tai textEdit -komponentti)
• Sovellus hakee viisi tuoreinta lämpötilaa ilmatieteenlaitoksen palvelusta.
• Lämpötilat näytetään ruudulla mittausajan kanssa
• https://www.ilmatieteenlaitos.fi/latauspalvelun-pikaohje


Esimerkki dataa Raahesta: 
http://opendata.fmi.fi/wfs/fin?
service=WFS&version=2.0.0&request=GetFeature&storedquery_id=fmi::observations::weather::ti
mevaluepair&place=Raahe&


ref:  https://www.ilmatieteenlaitos.fi/latauspalvelun-pikaohje

Vastauksessa on havaintoja yli kymmeneltä eri suureelta, mutta olemme kiinnostuneita vain lämpötilasta ja tuulen nopeudesta. Selvitämme suureparametrien nimet jokaisen wfs:member elementtilohkon sisällä olevan om:observedProperty elementin attribuuttina olevan linkin kautta. Tuulen nopeutta palauttaa suureparametri ws_10min ja lämpötilaa t2m.
http://opendata.fmi.fi/wfs/fin?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::observations::weather::timevaluepair&place=Raahe&parameters=ws_10min,t2m&

http://opendata.fmi.fi/wfs/fin?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::observations::weather::timevaluepair&place=Raahe&parameters=t2m&

<wml2:point>
<wml2:MeasurementTVP>
<wml2:time>2022-04-02T17:10:00Z</wml2:time>
<wml2:value>-3.4</wml2:value>
</wml2:MeasurementTVP>
</wml2:point>
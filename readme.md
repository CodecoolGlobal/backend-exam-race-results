Rögzítsünk versenyhelyezéseket adatbázisba!

# Adatbázis
Az adatbázisban két tábla van:

- competitor
    - id SERIAL PRIMARY KEY
    - competitor_name VARCHAR(255)
    
- race_result
    - id SERIAL PRIMARY KEY
    - competitor_id INTEGER FOREIGN KEY (competitor.id)
    - result INTEGER
    
Egy versenyző többször is indulhat a versenyen. Mindegyik helyezést meg kell őrizni az adatbázisban, de több próbálkozás esetén az utolsó helyezés számít.

# Java alkalmazás

Hozz létre egy `RaceResultRegister` nevű osztályt, ami az adatbázisba mentésért és az adatok feltöltéséért lesz felelős.
A RaceResultRegister konstruktorparaméterben kapja meg az adatbáziskapcsolathoz szükséges adatokat (url, felhasználó, jelszó).

Az osztálynak az alábbi publikus metódusai vannak:
- `public void saveResult(int competitorId, int result)`: elmenti az adatbázisba az elért helyezést (a versenyző esetleges eddigi heyezését nem írja felül)
- `public int getLastResult(int competitorId)`: adja vissza a versenyző legutóbbi helyezését, vagy ha még nem volt neki, akkor 0-t
- `public List<Integer> getAllResults(int competitorId)`: adja vissza a versenyző összes helyezését sorban, vagy üres listát, ha nincs még helyezése
- `public List<String> getMissingCompetitors()`: adja vissza az összes versenyző nevét, akinek még egyáltalán nincs helyezése rögzítve, vagy üres listát, ha mindenkihez lett mentve helyezés

A feladat egyszerűsítése miatt feltételezzük, hogy egyik metódus sem kap soha hibás adatokat (mindig létezik megadott id-val competitor).

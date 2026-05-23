# Sistem Integrat de Gestiune, Scraping și Procesare Documente pentru firma Autobrand

Acest proiect reprezintă o aplicație web completă dezvoltată în Spring Boot, concepută pentru a automatiza colectarea de date despre produse prin scraping web, integrarea cursurilor valutare în timp real, procesarea facturilor în format PDF și gestionarea acestor entități printr-o interfață securizată.

---

## 1. Structura Codului

Pentru identificarea unică și clasificarea riguroasă a componentelor în cadrul sistemului, s-a ales o abordare ce simulează un caz real, unde fiecare număr are o reprezentare simbolică.

Exemplu de analiză pe codul **172812F**:

* **17 (Primele două cifre):** Reprezintă mașinăria în cadrul căreia se operează (interval de valori acceptat: 00 - 23). Valoarea `17` identifică o *mașină auto personală*.
* **28 (Următoarele două cifre):** Reprezintă aria funcțională sau sistemul vehiculului (interval de valori acceptat: 00 - 53). Valoarea `28` identifică *sistemul de demarare*.
* **12 (Ultimele două cifre):** Reprezintă componenta specifică analizată (interval de valori acceptat: 00 - 26). Valoarea `12` identifică *comutatoarele electrice*.
* **F (Litera finală):** Reprezintă tipul sau producătorul componentei (interval de litere acceptat: A - F). Litera `F` identifică un comutator produs de *FEBI*.

Deși în instanțele curente de test s-a procesat un singur produs etalon, designul regex-ului a fost conceput hibrid ca o relație furnizor-produs. Modul de gândire al acestuia a fost de a simula mai multe produse pe o factură reală, fiind identificate după o anumită regulă. Mai multe componente au fost date și ca exemplu în cod, pentru a oferi o soluție cât mai completă pentru un caz real.

---

## 2. Arhitectura Tehnică și Implementarea

### Securitate și Autentificare (Spring Security)
* **Controlul Accesului:** Securizarea aplicației este realizată prin Spring Security. Rutele critice de administrare, precum apelul API către `/edit/{id}` sau operațiunile de ștergere și actualizare, sunt complet blocate pentru utilizatorii anonimi. Accesul este permis doar în urma parcurgerii cu succes a fluxului de login.
* **Managementul Parolelor:** Aplicația nu stochează niciodată parole în clar în baza de date. La înregistrare, parola este procesată printr-un algoritm de hashing (BCrypt). În timpul autentificării, sistemul preia hash-ul asociat utilizatorului din tabela `conturi` și validează dacă parola introdusă coincide cu cea stocată.

### Web Scraping și Automatizare (JSoup & Scheduler)
* **Colectarea Datelor:** S-a utilizat biblioteca JSoup pentru parsarea structurii DOM a site-ului țintă. În urma analizei codului HTML, au fost identificați selectorii CSS specifici fiecărui produs pentru a extrage precis atributele necesare: URL-ul imaginii, numele, descrierea și prețul de referință.
* **Navigarea și Logica de For:** S-a observat că platforma țintă este structurată pe 4 pagini, unde singura variabilă din URL era indexul paginii de la finalul request-ului. Colectarea completă s-a realizat prin implementarea unei bucle repetitive (for de la 1 la 4), urmată de o temporizare de siguranță (`Thread.sleep`) pentru a evita blocarea IP-ului.
* **Unicitatea Datelor:** Pentru a preveni duplicarea informațiilor în baza de date la rulări succesive, s-a asigurat constrângerea de unicitate la nivelul entității JPA și verificarea existenței anterioare în tabelă prin metode dedicate din repository (`existsByNume`).
* **Automatizare (Cronjob):** Procesul de scraping este complet automatizat prin intermediul unui task programat (`@Scheduled`). Acesta rulează la ore fixe în fundal, asigurând actualizarea constantă a catalogului de produse fără intervenție umană.

### Procesare Documente PDF (Apache PDFBox)
* **Parsarea Facturilor:** Procesarea și extragerea datelor din facturile încărcate de utilizatori se realizează cu ajutorul bibliotecii Apache PDFBox. Pentru a contracara citirea fragmentată a tabelelor complexe din PDF-uri, textul este sortat după coordonatele poziționale din pagină și normalizat într-un singur șir de caractere, extrăgându-se ulterior prețul net și cantitatea facturată printr-o căutare Regex.

### Sincronizarea Valutară și Dinamica Interfeței
* **Integrare Curs:** La momentul extragerii datelor, aplicația efectuează un apel API extern catre `https://frankfurter.dev/` pentru a obține cursul oficial EUR în RON din ziua respectivă, salvând atât rata de schimb, cât și valorile convertite.
* **Consistență Bidirecțională:** În cadrul ecranului de editare, s-a integrat un script JavaScript care ascultă în timp real modificările introduse de utilizator. Dacă se modifică prețul în EUR, valoarea în RON se recalculează automat pe baza cursului fix din baza de date și invers, asigurând coerența financiară a datelor înainte de salvare. De asemenea, pentru a proteja istoricul tranzacțional, câmpul aferent cursului valutar din interfață este blocat pe modul de strictă citire.

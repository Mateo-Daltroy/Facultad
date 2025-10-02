4.
    a. 3^2 no es de O(2^n) debido a que 3 escala mas rapido que 2, rindiendo la cota insuficiente

    b. n + log2(n) es de O(n) debido a que cuando clasificamos con numeracion asintotica, tenemos en cuenta el factor de mayor orden a traves de la regla de la suma y "n" tiene un mayor orden que log(n)

    c. n^(1/2) + 10^20 es de orden O(n^(1/2)) debido a que para todo T(n) que es un polinomio de grado k, T(n) = O(n^k)

    d. Dado que la notacion big-oh tiene como objetivo medir el cambio en la performance de un algoritmo conforme la muestra de elementos aumenta, tiene sentido tomar como orden la ecuacion de N que contempla el infinito, siendo esta 317, declaramos que el orden es O(1)

    e. Siendo que T(n) es un polinomio de grado 5 (el mayor exponente de n es 5), y todo polinomio de grado k sigue la propiedad de que T(n) = O(n^k), que en este caso corresponde a O(n^5)

    f. Verdadero, esa es una propiedad de big-oh con respecto a los polinomios

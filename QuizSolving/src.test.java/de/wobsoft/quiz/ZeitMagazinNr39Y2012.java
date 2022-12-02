package de.wobsoft.quiz;

import static org.assertj.core.api.Assertions.assertThat;

import java.io.IOException;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.logicng.datastructures.Assignment;
import org.logicng.formulas.Formula;
import org.logicng.formulas.FormulaFactory;

/**
 *
 * Denksport: Logelei
 * <p>
 * Von Zweistein
 * <p>
 * 20. September 2012 ZEITmagazin Nr. 39/2012
 * <p>
 * 
 * <pre>
Luise ist sehr kontaktfreudig und hat auf ihrer Kaffeefahrt in die Eifel fünf neue Bekanntschaften gemacht. 
Lustigerweise hat jeder, inklusive ihrer selbst, etwas anderes gekauft, und alle kommen aus einer anderen Stadt. 
Wieder zu Hause, kramt sie ihr Tagebuch hervor, wo sie sich während der Fahrt Notizen gemacht hat:

Der Reisebus hielt in sechs Städten, unter anderem in Ulm.

Maria stieg später in den Reisebus als Walter.

Die Heizdecke wurde nicht von einer Person aus Baden-Württemberg gekauft.

Weder die Person aus Karlsruhe noch die aus Pirmasens kaufte eine Tischdecke.


* 
* Die Person, die in Regensburg zustieg, kaufte ein Kochbuch.
* 
* Roswitha kommt aus Stuttgart.
* 
* Die Strümpfe wurden von einem Mann, das Beauty-Set von einer Frau gekauft.
* 
* Die weiteste Anreise hatte Gunter, die kürzeste Ursula.
* 
* Die Frau aus Ingolstadt kaufte eine Salatschleuder.
* 
* Jetzt versucht sie sich zu erinnern, wer aus welcher Stadt kam und wer was gekauft hatte. 
* Können Sie das für sie herausfinden?
 * 
 
 * Lösung:
1	[Gunter, Kochbuch, Regensburg]
2	[Ingolstadt, Luise, Salatschleuder]
3	[Strümpfe, Ulm, Walter]
4	[Roswitha, Stuttgart, Tischdecke]
5	[Beauty_Set, Karlsruhe, Maria]
6	[Heizdecke, Pirmasens, Ursula]
 * 
 * 
 * 
 * </pre>
 */
class ZeitMagazinNr39Y2012 extends Quiz {
	private static final int n = 6;



	enum City {
		Stuttgart, Pirmasens, Ingolstadt, Regensburg, Ulm, Karlsruhe
	};

	enum Passenger {
		Roswitha, Gunter, Ursula, Maria, Walter, Luise
	};

	enum Good {
		Salatschleuder, Strümpfe, Beauty_Set, Kochbuch, Tischdecke, Heizdecke
	};

	@BeforeEach
	public void setUp() {
		f = new FormulaFactory();
	}

	@AfterEach
	public void tearDown() {

	}

	@Test
	void solve() throws IOException {

		Formula cities = permutation(toArray(City.class), n);
		Formula passengers = permutation(toArray(Passenger.class), n);
		Formula goods = permutation(toArray(Good.class), n);

		// Roswitha kommt aus Stuttgart.
		Formula p1 = equivalence(Passenger.Roswitha, City.Stuttgart, n);

		// Maria stieg später in den Reisebus als Walter.
		Formula p2 = gt(Passenger.Maria, Passenger.Walter, n);

		// Die Heizdecke wurde nicht von einer Person aus Baden-Württemberg gekauft.
		Formula p3 = f.and(//
				nand(Good.Heizdecke, City.Karlsruhe, n), //
				nand(Good.Heizdecke, City.Stuttgart, n), //
				nand(Good.Heizdecke, City.Ulm, n));//

		// Die Person, die in Regensburg zustieg, kaufte ein Kochbuch.
		Formula p4 = equivalence(Good.Kochbuch, City.Regensburg, n);


		// Die weiteste Anreise hatte Gunter, die kürzeste Ursula.
		Formula p5 = pair(Passenger.Gunter, 1);
		Formula p6 = pair(Passenger.Ursula, 6);

		// Formula p61 = f.not(pair(Good.Strümpfe, 1));

		// Weder die Person aus Karlsruhe noch die aus Pirmasens kaufte eine Tischdecke.
		Formula p7 = nand(Good.Tischdecke, City.Karlsruhe, n);
		Formula p8 = nand(Good.Tischdecke, City.Pirmasens, n);

		// Die Frau aus Ingolstadt kaufte eine Salatschleuder.
		Formula p9 = equivalence(Good.Salatschleuder, City.Ingolstadt, n);

		// Die Strümpfe wurden von einem Mann, das Beauty-Set von einer Frau gekauft.
		Formula p10 = f.or(//
				equivalence(Good.Strümpfe, Passenger.Walter, n), //
				equivalence(Good.Strümpfe, Passenger.Gunter, n));

		// Die Strümpfe wurden von einem Mann, das Beauty-Set von einer Frau gekauft.
		Formula p11 = f.or(//
				equivalence(Good.Beauty_Set, Passenger.Luise, n), //
				equivalence(Good.Beauty_Set, Passenger.Ursula, n), //
				equivalence(Good.Beauty_Set, Passenger.Maria, n), //
				equivalence(Good.Beauty_Set, Passenger.Roswitha, n));

		// Die Frau aus Ingolstadt kaufte eine Salatschleuder.
		Formula p12 = f.or(//
				equivalence(City.Ingolstadt, Passenger.Luise, n), //
				equivalence(City.Ingolstadt, Passenger.Ursula, n), //
				equivalence(City.Ingolstadt, Passenger.Maria, n), //
				equivalence(City.Ingolstadt, Passenger.Roswitha, n));

		
		// abgleitet Bedingungen
		// Folgerung: Nur Luise weiß, wer wann eingestiegen ist, also muss sie
		// spätestens an der 2. Station eingestiegen sein.
		Formula p13 = equivalence(Passenger.Luise, City.Ingolstadt, n);

	
		// daraus folgt: Sie muss in Ingolstat eingestiegen sein, da Gunter die weiteste
		// Anreise hat.
		Formula p14 = pair(City.Ingolstadt, 2);
		Formula p15 = equivalence(Passenger.Gunter, City.Regensburg, n);

		// "Kaffeefahrt in die Eifel" -> also muss Pirmasens der letute Ort sein, an dem jemand zufestiegen ist.
		Formula p16 = pair(City.Pirmasens, 6);

		// Annahme, dass der Bus die kürzeste Route fährt
		Formula p17 = pair(City.Karlsruhe, 5);
		Formula p18 = pair(City.Stuttgart, 4);

		// alle Bedingumgen sind gültig, also AND.
		Formula formula = f.and(passengers, goods, cities, p1, p2, p3, p4, p5, p6, p7, p8, p9, p10, p11,
				p12, p13, p14, p15, p16, p17, p18);

		// solve
		List<Assignment> models = SolverUtils.solve(formula);

		assertThat(models).isNotNull().hasSize(1);
		printModel(models.get(0));
	}
}
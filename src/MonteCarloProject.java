import java.util.Random;

/**
 * Monte Carlo Method - Randomized Algorithm Project
 *
 * Problem: In a randomly generated array of N elements,
 * estimate the number of elements whose value is 0 modulo 7.

 */
public class MonteCarloProject {

    // ---------------------------------------------------------------
    // Sabitler
    // ---------------------------------------------------------------
    static final long   STUDENT_NUMBER = 5240505030L; // <-- STUDENT ID NUMBER
    static final int    N              = 100_000; // Data size (last digit < 5)
    static final int    K              = 1_000;   // The number of samples taken for each prediction
    static final int    EXPERIMENTS    = 100;     // Number of repetitions
    static final int    WARMUP         = 20;      // JVM warm-up cycles (not included in the measurement)
    static final double ERROR_MARGIN   = 0.05;    //Theoretical error threshold (5%)

    public static void main(String[] args) {

        // -----------------------------------------------------------
        // 1. Separate Random objects
        //    arrayRandom  → generates an array (fixed, repeatable)
        //    sampleRandom → used in Monte Carlo sampling
        // -----------------------------------------------------------
        Random arrayRandom  = new Random(STUDENT_NUMBER);
        Random sampleRandom = new Random(STUDENT_NUMBER + 1);

        // -----------------------------------------------------------
        // 2. Creating an array
        // -----------------------------------------------------------
        int[] array = new int[N];
        for (int i = 0; i < N; i++) {
            array[i] = arrayRandom.nextInt(1_000_000);
        }

        // -----------------------------------------------------------
        // 3. Actual (final) count
        // -----------------------------------------------------------
        int realCount = exactCount(array);

        // -----------------------------------------------------------
        // 4. Theoretical calculations
        //    p   = probability that mod7==0 (based on actual data)
        //    SE  = standard error
        //    P(|relative error| > ERROR_MARGIN) → Chebyshev upper bound
        // -----------------------------------------------------------
        double p             = (double) realCount / N;          // actual rate
        double variance      = p * (1 - p) / K;                 // p̂ variance
        double stdError      = Math.sqrt(variance);             // standard error
        // Chebyshev: P(|p̂ - p| > ε) ≤ Var(p̂) / ε²
        double epsilon       = ERROR_MARGIN * p;                // absolute error threshold
        double pErrorTheory  = variance / (epsilon * epsilon);  // upper bound (constrained to ≤1)
        pErrorTheory         = Math.min(pErrorTheory, 1.0);

        // -----------------------------------------------------------
        // 5. JVM Warmup (not included in the measurement)
        // -----------------------------------------------------------
        for (int i = 0; i < WARMUP; i++) {
            monteCarloEstimate(array, K, sampleRandom);
        }

        // -----------------------------------------------------------
        // 6. 100 experiments: measurement of duration and error rate
        // -----------------------------------------------------------
        double totalError     = 0;
        double totalTime      = 0;
        int    errorCount     = 0;   // Number of errors exceeding %ERROR_MARGIN
        double[] times        = new double[EXPERIMENTS];
        double[] errorRates   = new double[EXPERIMENTS];

        for (int i = 0; i < EXPERIMENTS; i++) {
            long start     = System.nanoTime();
            int  estimated = monteCarloEstimate(array, K, sampleRandom);
            long end       = System.nanoTime();

            double timeMs    = (end - start) / 1_000_000.0;
            double errorRate = Math.abs(realCount - estimated) / (double) realCount;

            times[i]      = timeMs;
            errorRates[i] = errorRate;

            totalError += errorRate;
            totalTime  += timeMs;

            if (errorRate > ERROR_MARGIN) {
                errorCount++;
            }
        }

        // -----------------------------------------------------------
        // 7. Statistics
        // -----------------------------------------------------------
        double avgError       = totalError / EXPERIMENTS;
        double avgTime        = totalTime  / EXPERIMENTS;
        double stdDevTime     = standardDeviation(times,      avgTime);
        double stdDevError    = standardDeviation(errorRates, avgError);
        double expErrorRate   = (double) errorCount / EXPERIMENTS; // experimental P(error)

        // -----------------------------------------------------------
        // 8. Console output
        // -----------------------------------------------------------
        System.out.println("=======================================================");
        System.out.println("       MONTE CARLO PROJESİ - SONUÇ RAPORU");
        System.out.println("=======================================================");
        System.out.printf("Öğrenci No (Seed)   : %d%n", STUDENT_NUMBER);
        System.out.printf("Veri Boyutu (N)     : %,d%n", N);
        System.out.printf("Örnek Sayısı (K)    : %,d%n", K);
        System.out.printf("Deney Sayısı        : %d%n",  EXPERIMENTS);
        System.out.println("-------------------------------------------------------");
        System.out.println("[ VERİ ]");
        System.out.printf("Gerçek Sayı         : %d%n",    realCount);
        System.out.printf("Gerçek Oran (p)     : %.6f%n",  p);
        System.out.println("-------------------------------------------------------");
        System.out.println("[ TEORİK HESAPLAR ]");
        System.out.printf("p̂ Varyansı          : %.8f%n",  variance);
        System.out.printf("Standart Hata (SE)  : %.6f%n",  stdError);
        System.out.printf("Hata Eşiği ε        : %.2f%%%n", ERROR_MARGIN * 100);
        System.out.printf("P(|hata| > ε) üst sınırı [Chebyshev]: %.4f (%.2f%%)%n",
                pErrorTheory, pErrorTheory * 100);
        System.out.println("-------------------------------------------------------");
        System.out.println("[ DENEYSEL SONUÇLAR ]");
        System.out.printf("Ort. Hata Oranı     : %.6f (%.4f%%)%n", avgError, avgError * 100);
        System.out.printf("Ort. Çalışma Süresi : %.4f ms%n", avgTime);
        System.out.printf("Süre Std Sapması    : %.4f ms%n", stdDevTime);
        System.out.printf("Hata Std Sapması    : %.6f%n",    stdDevError);
        System.out.printf("Deneysel P(error>%.0f%%): %.4f (%d/%d deney)%n",
                ERROR_MARGIN * 100, expErrorRate, errorCount, EXPERIMENTS);
        System.out.println("-------------------------------------------------------");
        System.out.println("[ TEORİK vs DENEYSEL KARŞILAŞTIRMA ]");
        System.out.printf("Teorik  P(error)    : %.4f%n", pErrorTheory);
        System.out.printf("Deneysel P(error)   : %.4f%n", expErrorRate);
        System.out.printf("Fark                : %.4f%n", Math.abs(pErrorTheory - expErrorRate));
        System.out.println("Not: Chebyshev üst sınır olduğundan deneysel değer");
        System.out.println("     teorik değerden küçük veya eşit olması beklenir.");
        System.out.println("=======================================================");
    }

    // ---------------------------------------------------------------
    // Actual time complexity: O(N)
    // ---------------------------------------------------------------
    static int exactCount(int[] array) {
        int count = 0;
        for (int value : array) {
            if (value % 7 == 0) count++;
        }
        return count;
    }

    // ---------------------------------------------------------------
    // Monte Carlo simulation: Take K random samples, multiply by the probability, and repeat N times
    // ---------------------------------------------------------------
    static int monteCarloEstimate(int[] array, int k, Random random) {
        int sampleCount = 0;
        for (int i = 0; i < k; i++) {
            int index = random.nextInt(array.length);
            if (array[index] % 7 == 0) sampleCount++;
        }
        double ratio = sampleCount / (double) k;
        return (int) Math.round(ratio * array.length);
    }

    // ---------------------------------------------------------------
    // Standard deviation  (population)
    // ---------------------------------------------------------------
    static double standardDeviation(double[] values, double mean) {
        double sum = 0;
        for (double v : values) {
            sum += (v - mean) * (v - mean);
        }
        return Math.sqrt(sum / values.length);
    }
}

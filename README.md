# Monte Carlo Randomized Algorithm

A Java implementation of a Monte Carlo randomized algorithm for probabilistic estimation on large datasets.

## Project Description

This project demonstrates the use of Monte Carlo randomized algorithms to estimate the number of elements satisfying a specific condition in a large randomly generated dataset.

The implemented problem is:

> Estimating how many elements in a randomly generated array are divisible by 7.

The project also includes:

- Theoretical probability analysis
- Experimental error analysis
- Runtime performance measurements
- Standard deviation calculations
- Comparison between theoretical and experimental results

---

## Technologies

- Java
- Randomized Algorithms
- Monte Carlo Method
- Probability Theory
- Statistical Analysis

---

## Algorithm Overview

Instead of scanning the entire dataset repeatedly, the algorithm:

1. Generates a large random dataset
2. Randomly samples K elements
3. Estimates the ratio of successful samples
4. Predicts the total count probabilistically

This approach significantly reduces computational cost.

---

## Time Complexity

| Method | Complexity |
|---|---|
| Exact Counting | O(N) |
| Monte Carlo Estimation | O(K) |

Where:

- `N = dataset size`
- `K = sample size`
- `K << N`

---

## Theoretical Analysis

The project includes:

- Expected value analysis
- Error probability estimation
- Chebyshev inequality upper bound
- Experimental validation over 100 runs

---

## Example Output

```text
=======================================================
       MONTE CARLO PROJECT - RESULTS
=======================================================
Average Error Rate     : 0.0214
Average Runtime        : 0.1432 ms
Experimental P(error) : 0.0300
=======================================================
```

---

## Features

- Reproducible random seed
- JVM warmup phase
- Statistical runtime analysis
- Standard deviation calculations
- Experimental vs theoretical comparison

---

## Author

Software Engineering Student

---

## License

This project is developed for educational and academic purposes.

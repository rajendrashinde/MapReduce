
for L in 50 100 150 200 250
do
echo "package lsh;"
echo "public class Parameters {"
echo "\t double W = 0.5;"
echo "\t int k = 10;"
echo "int L = $L;"
echo "int d = 100;"
echo "double u = 0.6;"
echo "double c = 2.0;"
echo "double D = 1.0;"
done
echo "}"

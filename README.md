This README contain the instruction to launch DISGROU.

The program requires 4 parameters :
	1) The path to the data file. The file needs to be in the CSV format, with the labels of the attributes in the first line and numerical values.
	2) The label of the target value
	3) The threshold beta to make the raw selectors. It represents the percentage of elements needed to consider a raw selector large enough.
	4) The number of cores which has to be used

The outputs of the stdout are the subgroups classed by their score.
The output format consists of a list of selectors (attributes and intervals) with the score of the subgroup.

The file Results.txt is also created with the same outputs.

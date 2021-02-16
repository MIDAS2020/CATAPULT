#pragma once
//copyright version 1.0 readGraph.h,
//This file read graphs from file

#ifndef READGRAPHS_H
#define READGRAPHS_H
#include <vector>
#include <fstream>
#include <iostream>
#include <string>
#include <boost/graph/iteration_macros.hpp>
#include <boost/graph/adjacency_list.hpp>
#include <boost/graph/property_iter_range.hpp>
#include <boost/config.hpp>
#include <boost/graph/graphviz.hpp>
#include <boost/graph/graph_traits.hpp>
#include <boost/graph/properties.hpp>
#include <numeric>
#include <iterator>
using namespace std;
using namespace boost;
namespace graphsName {
	struct Vertex_Info {
		std::string name;
		std::vector<int> containList;
	};
	typedef adjacency_list<vecS, vecS, undirectedS, Vertex_Info
		, no_property> graph;

	typedef graph_traits<graph>::vertices_size_type vertices_number;
	typedef graph_traits<graph>::vertex_descriptor vertex_t;
	typedef graph_traits<graph>::edge_descriptor edge_t;
	/*static    string vertexLable[] = {
	"C", "O", "Cu", "N", "S", "P", "Cl", "Zn", "B", "Br", "Co", "Mn", "As", "Al", "Ni", "Se",
	"Si", "V", "Sn", "I", "F", "Li", "Sb", "Fe", "Pd", "Hg", "Bi", "Na", "Ca", "Ti", "Ho", "Ge",
	"Pt", "Ru", "Rh", "Cr", "Ga", "K", "Ag", "Au", "Tb", "Ir", "Te", "Mg", "Pb", "W", "Cs", "Mo",
	"Re", "Cd", "Os", "Pr", "Nd", "Sm", "Gd", "Yb", "Er", "U", "Tl", "Ac"
	};
	*/
	//Cs Cu Yb Cl Pt Pr Co Cr Li Cd Ce Hg Hf La Lu 
	//Pd Tl Tm Ho Pb * Ti Te Dy Ta Os Mg Tb Au Se 
	//F Sc Fe In Si B C As Sn N Ba O Eu H Sr I Mo 
	//Mn K Ir Er Ru Ag W V Ni P S Nb Y Na Sb Al Ge Rb Re Gd Ga Br Rh Ca Bi Zn Zr

	static    string vertexLable[] = {
		"Cs", "Cu", "Yb", "Cl", "Pt", "Pr", "Co", "Cr", "Li", "Cd", "Ce", "Hg", "Hf", "La", "Lu",
		"Pd", "Tl", "Tm", "Ho", "Pb", "*", "Ti", "Te", "Dy", "Ta", "Os", "Mg", "Tb", "Au", "Se",
		"F", "Sc", "Fe", "In", "Si", "B", "C", "As", "Sn", "N", "Ba", "O", "Eu", "H", "Sr", "I", "Mo",
		"Mn", "K", "Ir", "Er", "Ru", "Ag", "W", "V", "Ni", "P", "S", "Nb",
		"Y", "Na", "Sb", "Al", "Ge", "Rb", "Re", "Gd", "Ga", "Br", "Rh", "Ca", "Bi", "Zn", "Zr"
	};

	class ReadGraphs {
		//test	 
	public:
		ReadGraphs() {

		}
		void writegraphs(char* filename, int indexn);
		char readcommand(FILE *file);
		int readInt(FILE *input);
		graph  readFromFile(FILE*  input);
		ReadGraphs(string newfilename) {
			this->filename = newfilename;
		}
		void readGraphsFromFile(FILE* input, int size);

		int getSize() {
			return this->graphs.size();
		}
		vector<graph> getGraphs() {
			return graphs;
		}
	private:
		string filename;
		vector<graph>  graphs;


	};
}
#endif

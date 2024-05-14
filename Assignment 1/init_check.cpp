#include<iostream>
#include<fstream>
#include<stdlib.h>

using namespace std;

int main(int argc, char const *argv[])
{
    if (argc != 2) {
        cout << "Program usage: init <filename>";
        return -1;
    }

    string filename = argv[1];

    return 0;
}

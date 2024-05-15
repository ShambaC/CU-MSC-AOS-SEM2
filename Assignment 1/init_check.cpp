#include<iostream>
#include<fstream>
#include<stdlib.h>
#include<string>

using namespace std;

int main(int argc, char const *argv[])
{
    if (argc != 2) {
        cout << "Program usage: init help | init <filename>";
        return -1;
    }

    string helpText = "help";

    if (helpText.compare(argv[1]) == 0) {
        cout << "TODO FORMATED README";
        return 0;
    }

    string fileName = argv[1];
    cout << fileName;

    return 0;
}

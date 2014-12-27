#!/usr/bin/python

# This is a crazy hack to convert Weka's arff into caffe's leveldb
import leveldb
import caffe_pb2
import struct

def read(filename):
    fl = open(filename)
    label_index = 0
    label_found = False
    line = fl.readline()
    while line:
        if line.startswith("@data"):
            break
        elif not label_found and line.startswith("@attribute"):
            if line.split()[1] == "CORRECT":
                label_found = True
            else:
                label_index += 1
    
    for line in fl:
        if line.strip():
            l = [float(x.replace("?", "NaN")) for x in line.split(',')]
            label = l.pop(label_index)
            yield (l, label)

def transform(prev):
    d = caffe_pb2.Datum()
    d.channels = 1
    d.height = 1
    d.width = 2064
    totals = [0] * d.width
    for entry, label in prev:
        totals = [t+e for t, e in zip(totals, entry)]
        d.data = struct.pack("2064d", *entry)
        d.label = label
        yield d.SerializeToString()
        
    d.data = struct.pack("2064d", *totals)
    open("watson_mean.binaryproto", "w").write(d.SerializeToString())
    

def write(filename, prev):
    ldb = leveldb.LevelDB(filename=filename, create_if_missing=True, error_if_exists=True)
    for key, entry in enumerate(prev):
        ldb.Put(str(key).zfill(5), entry)


if __name__ == "__main__":
    import sys
    write(sys.argv[2], transform(read(sys.argv[1])))
# ModifiedPriorityQueue
 This repository contains a modified implementation of the build-in PriorityQueue java class. It combines a HashMap with it, to allow O(1) for contains.
 
 Here is the [medium article](https://axel7083.medium.com/solving-k-max-problem-combining-priorityqueue-and-hashmap-data-structure-5ff42e0cf510) I wrote corresponding to this repository.

# Usage

You can use your own Comparator as a constructor argument.

kBest represents the number of elements you want to keep.
```Java
ModifiedPriorityQueue<Integer> mPQueue = new ModifiedPriorityQueue<Integer>(kBest);
for(int val : data)
    if(mPQueue.size() < kBest)
        mPQueue.add(val);
    else {
        int first = mPQueue.peek();
        if(val > first)
            mPQueue.add(val);
    }      
```

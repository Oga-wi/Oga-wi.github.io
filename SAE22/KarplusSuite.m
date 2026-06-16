function KarplusSuite(F1, F2)
    Fe = 44100;
    a = 0.99;
    
    s1 = Karplus(F1, a, 1);
    s2 = Karplus(F2, a, 1);
    
    accord = [s1 , s2];
    
    soundsc(accord, Fe);
end
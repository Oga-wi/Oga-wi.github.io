function [Y]=effet_chorus(X,FS,coeff)
% realisation de l'effet
D1=FS/10;
D2=3*FS/1000;
long = length(X);
E=repmat(0,long,1);
x = rand(1,1);
for i=1:long
    d = i + round(D1 + (D2 - D1)* x(1,1));
    if ( d > long ) 
        d = long;
    end
    E(i) = X(d);
end
% ajout de l'effet au signal
Y=X+coeff*E;
    
%Normalistion du signal de sortie
sx=sum(abs(X));
sy=sum(abs(Y));
sr=repmat(sx/sy,length(x),1);
Y=sr.*Y;

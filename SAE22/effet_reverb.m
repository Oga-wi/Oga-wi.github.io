% effet Reverb
function [Y]=effet_reverb(X,FS,temps,attenuation)
    %   temps est en ms
% cr�ation du filtre
temps=temps/1000;
del=round(temps*FS);
b=zeros(1 , 4*del);
b(1)=1;
a=[1];
b(4*del)= attenuation;
b(3*del)= attenuation + 0.07;
b(2*del)= attenuation + 0.07;
b(del)= attenuation + 0.08;
% filtrage
Y=filter(b,a,X);
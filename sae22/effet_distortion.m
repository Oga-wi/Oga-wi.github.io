function [Y]=effet_distortion(X,FS,A)
%distorsion:
Y=(atan((0.1+2*A)*X));
%Normalistion du signal
sx=sum(abs(X));
sy=sum(abs(Y));
sr=repmat(sx/sy,length(X),1);
Y=sr.*Y;
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% EFFET FLANGE %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
% le flange est un retard variable de courte dur�e (de 1 � 10 ms) qui est ajout� au signal d'entr�e
% la dur�e de ce retard oscille en tres basse frequence
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
function [Y]=effet_flange(X,FS,puissance)
    %   Y  = spectre du signal de sortie
    %   X  = spectre du signal d'entr�
    %   FS = frequence d'echantillonnage
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
decalage = round(puissance*FS/10000);                           %   le decalage est determin� puis convertit en nombre de points du spectre
n = 1 : length(X);                                              %   n est une matrice ligne avec les entiers de 1 � length(X)+decalage
z = zeros(decalage,1);                                          %   z est une matrice ligne de 0 de la taille du decalage
Y = [z;X];                                                      %   on rajoute z avant et apres X
teta = 2 * pi / round(FS*puissance/10);                         %   teta = 2Pi / partie entiere de periode*FS - la periode vaut puissance/10
decalage = decalage - round((decalage/2) * (1-cos(teta*n)));    %   on cree une matrice contenant le decalage pour chaque point du spectre
                                                                %
Y = Y(n) + Y(n+decalage);                                       %   modification du signal avec flange
Y = Y * max(abs(X))/max(abs(Y));                                %   normalisation du signal de sortie
%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%

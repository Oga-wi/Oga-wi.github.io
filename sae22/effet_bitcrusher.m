function [Y] = effet_bitcrusher(X, bits)
    %   Y    = Signal de sortie dégradé
    %   X    = Signal d'entrée
    %   bits = Résolution désirée (ex: 8, 4, ou 2 pour un effet extrême)

    % Détermination du nombre de paliers (ex: 4 bits = 16 paliers)
    paliers = 2^bits;
    
    max_val = max(abs(X));
    if max_val == 0
        Y = X; 
        return;
    end
    X_norm = X / max_val;
    Y = round(X_norm * paliers) / paliers;
    Y = Y * max_val / max(abs(Y));
end

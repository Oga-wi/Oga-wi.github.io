function s_total = Karplus(F0, a, repetition)
    Fe = 44100;
    d = .25;
    L = round(Fe/F0);
    N = round(d*Fe);
    e = [randn(1,L), zeros(1,N-L)];
    s = zeros(1,N);
    x = zeros(1,N);
    y = zeros(1,N);
    DL = zeros(1,L);
    s_total = [];

    for i = 1:repetition
        for k = 2:N
            x(k) = DL(L);
            y(k) = (a/2) * (x(k) + x(k-1));
            s(k) = e(k) + y(k);
            DL(2:L) = DL(1:L-1);
            DL(1) = s(k);
        end
        s_total = [s_total, s];
    end

    % soundsc(s_total, Fe);
end
print Test du tryRead avec le template u1 (devrait renvoyer null)
new u1 [ ?String ?String ]
tryRead u2 u1
print u2

print Ecriture d'un tuple attendu par le TestShell1
new u3 [ 4 "r" ]
write u3

print Test du tryRead avec le template u1 (devrait renvoyer [ "r" "t" ])
new u4 [ ?String ?String ]
tryRead u5 u4
print u5

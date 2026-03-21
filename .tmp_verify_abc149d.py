import itertools, random
def greedy(n,k,r,s,p,t):
    won=[False]*n
    ans=0
    for i,ch in enumerate(t):
        score={'r':p,'s':r,'p':s}[ch]
        if i>=k and t[i]==t[i-k] and won[i-k]:
            continue
        ans += score
        won[i] = True
    return ans
def brute(n,k,r,s,p,t):
    hands='rsp'
    best=-1
    for seq in itertools.product(hands, repeat=n):
        total=0
        for i,h in enumerate(seq):
            opp=t[i]
            win=(h=='r' and opp=='s') or (h=='s' and opp=='p') or (h=='p' and opp=='r')
            if not win:
                continue
            if i>=k and h==seq[i-k]:
                continue
            total += {'r':r,'s':s,'p':p}[h]
        best=max(best,total)
    return best
for n in range(1,8):
    for k in range(1,n+1):
        for _ in range(200):
            r,s,p=[random.randint(1,9) for _ in range(3)]
            t=''.join(random.choice('rsp') for _ in range(n))
            g=greedy(n,k,r,s,p,t)
            b=brute(n,k,r,s,p,t)
            if g!=b:
                print('mismatch', n,k,r,s,p,t,g,b)
                raise SystemExit(1)
print('OK')
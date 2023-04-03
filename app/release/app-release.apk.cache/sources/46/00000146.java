package ag;

/* renamed from: ag.c */
/* loaded from: classes.dex */
public final class IntTree<V> {

    /* renamed from: f */
    public static final IntTree<Object> f920f = new IntTree<>();

    /* renamed from: a */
    public final long f921a;

    /* renamed from: b */
    public final V f922b;

    /* renamed from: c */
    public final IntTree<V> f923c;

    /* renamed from: d */
    public final IntTree<V> f924d;

    /* renamed from: e */
    public final int f925e;

    public IntTree() {
        this.f925e = 0;
        this.f921a = 0L;
        this.f922b = null;
        this.f923c = null;
        this.f924d = null;
    }

    /* renamed from: a */
    public final V m8198a(long j) {
        if (this.f925e == 0) {
            return null;
        }
        long j2 = this.f921a;
        if (j < j2) {
            return this.f923c.m8198a(j - j2);
        }
        if (j > j2) {
            return this.f924d.m8198a(j - j2);
        }
        return this.f922b;
    }

    /* renamed from: b */
    public final IntTree m8197b(long j, ConsPStack consPStack) {
        if (this.f925e == 0) {
            return new IntTree(j, consPStack, this, this);
        }
        long j2 = this.f921a;
        int i = (j > j2 ? 1 : (j == j2 ? 0 : -1));
        if (i < 0) {
            return m8196c(this.f923c.m8197b(j - j2, consPStack), this.f924d);
        }
        if (i > 0) {
            return m8196c(this.f923c, this.f924d.m8197b(j - j2, consPStack));
        }
        if (consPStack == this.f922b) {
            return this;
        }
        return new IntTree(j, consPStack, this.f923c, this.f924d);
    }

    /* renamed from: c */
    public final IntTree<V> m8196c(IntTree<V> intTree, IntTree<V> intTree2) {
        if (intTree == this.f923c && intTree2 == this.f924d) {
            return this;
        }
        long j = this.f921a;
        V v = this.f922b;
        int i = intTree.f925e;
        int i2 = intTree2.f925e;
        if (i + i2 > 1) {
            if (i >= i2 * 5) {
                IntTree<V> intTree3 = intTree.f923c;
                IntTree<V> intTree4 = intTree.f924d;
                if (intTree4.f925e < intTree3.f925e * 2) {
                    long j2 = intTree.f921a;
                    return new IntTree<>(j2 + j, intTree.f922b, intTree3, new IntTree(-j2, v, intTree4.m8195d(intTree4.f921a + j2), intTree2));
                }
                IntTree<V> intTree5 = intTree4.f923c;
                IntTree<V> intTree6 = intTree4.f924d;
                long j3 = intTree4.f921a;
                long j4 = intTree.f921a + j3 + j;
                V v2 = intTree4.f922b;
                IntTree intTree7 = new IntTree(-j3, intTree.f922b, intTree3, intTree5.m8195d(intTree5.f921a + j3));
                long j5 = intTree.f921a;
                long j6 = intTree4.f921a;
                return new IntTree<>(j4, v2, intTree7, new IntTree((-j5) - j6, v, intTree6.m8195d(intTree6.f921a + j6 + j5), intTree2));
            } else if (i2 >= i * 5) {
                IntTree<V> intTree8 = intTree2.f923c;
                IntTree<V> intTree9 = intTree2.f924d;
                if (intTree8.f925e < intTree9.f925e * 2) {
                    long j7 = intTree2.f921a;
                    return new IntTree<>(j7 + j, intTree2.f922b, new IntTree(-j7, v, intTree, intTree8.m8195d(intTree8.f921a + j7)), intTree9);
                }
                IntTree<V> intTree10 = intTree8.f923c;
                IntTree<V> intTree11 = intTree8.f924d;
                long j8 = intTree8.f921a;
                long j9 = intTree2.f921a;
                long j10 = j8 + j9 + j;
                V v3 = intTree8.f922b;
                IntTree intTree12 = new IntTree((-j9) - j8, v, intTree, intTree10.m8195d(intTree10.f921a + j8 + j9));
                long j11 = intTree8.f921a;
                return new IntTree<>(j10, v3, intTree12, new IntTree(-j11, intTree2.f922b, intTree11.m8195d(intTree11.f921a + j11), intTree9));
            }
        }
        return new IntTree<>(j, v, intTree, intTree2);
    }

    /* renamed from: d */
    public final IntTree<V> m8195d(long j) {
        if (this.f925e != 0 && j != this.f921a) {
            return new IntTree<>(j, this.f922b, this.f923c, this.f924d);
        }
        return this;
    }

    public IntTree(long j, V v, IntTree<V> intTree, IntTree<V> intTree2) {
        this.f921a = j;
        this.f922b = v;
        this.f923c = intTree;
        this.f924d = intTree2;
        this.f925e = intTree.f925e + 1 + intTree2.f925e;
    }
}
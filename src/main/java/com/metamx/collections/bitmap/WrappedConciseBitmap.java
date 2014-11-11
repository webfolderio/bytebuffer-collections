package com.metamx.collections.bitmap;

import com.google.common.primitives.Ints;
import it.uniroma3.mat.extendedset.intset.ConciseSet;
import it.uniroma3.mat.extendedset.intset.ImmutableConciseSet;
import it.uniroma3.mat.extendedset.intset.IntSet;
import org.roaringbitmap.IntIterator;

import java.nio.ByteBuffer;

public class WrappedConciseBitmap implements MutableBitmap
{
  /**
   * Underlying bitmap.
   */
  private ConciseSet bitmap;

  /**
   * Create a new WrappedConciseBitmap wrapping an empty  ConciseSet
   */
  public WrappedConciseBitmap()
  {
    this.bitmap = new ConciseSet();
  }

  /**
   * Create a bitmap wrappign the given bitmap
   *
   * @param conciseSet bitmap to be wrapped
   */
  public WrappedConciseBitmap(ConciseSet conciseSet)
  {
    this.bitmap = conciseSet;
  }

  public ConciseSet getBitmap()
  {
    return bitmap;
  }

  @Override
  public byte[] toBytes()
  {
    return ImmutableConciseSet.newImmutableFromMutable(bitmap).toBytes();
  }

  @Override
  public int compareTo(ImmutableBitmap other)
  {
    return bitmap.compareTo(((WrappedConciseBitmap) other).getBitmap());
  }

  @Override
  public void clear()
  {
    bitmap.clear();
  }

  @Override
  public void or(MutableBitmap mutableBitmap)
  {
    WrappedConciseBitmap other = (WrappedConciseBitmap) mutableBitmap;
    ConciseSet unwrappedOtherBitmap = other.bitmap;
    bitmap.addAll(unwrappedOtherBitmap);
  }

  @Override
  public void and(MutableBitmap mutableBitmap)
  {
    WrappedConciseBitmap other = (WrappedConciseBitmap) mutableBitmap;
    ConciseSet unwrappedOtherBitmap = other.bitmap;
    bitmap = bitmap.intersection(unwrappedOtherBitmap);
  }

  @Override
  public void xor(MutableBitmap mutableBitmap)
  {
    WrappedConciseBitmap other = (WrappedConciseBitmap) mutableBitmap;
    ConciseSet unwrappedOtherBitmap = other.bitmap;
    bitmap = bitmap.symmetricDifference(unwrappedOtherBitmap);
  }

  @Override
  public void andNot(MutableBitmap mutableBitmap)
  {
    WrappedConciseBitmap other = (WrappedConciseBitmap) mutableBitmap;
    ConciseSet unwrappedOtherBitmap = other.bitmap;
    bitmap = bitmap.difference(unwrappedOtherBitmap);
  }

  @Override
  public int getSizeInBytes()
  {
    return bitmap.getWords().length * Ints.BYTES;
  }

  @Override
  public void add(int entry)
  {
    bitmap.add(entry);
  }

  @Override
  public int size()
  {
    return bitmap.size();
  }

  @Override
  public void serialize(ByteBuffer buffer)
  {
    byte[] bytes = ImmutableConciseSet.newImmutableFromMutable(bitmap).toBytes();
    buffer.putInt(bytes.length);
    buffer.put(bytes);
  }

  @Override
  public String toString()
  {
    return getClass().getSimpleName() + bitmap.toString();
  }

  @Override
  public void remove(int entry)
  {
    bitmap.remove(entry);
  }

  @Override
  public IntIterator iterator()
  {
    final IntSet.IntIterator i = bitmap.iterator();
    return new IntIterator()
    {
      @Override
      public IntIterator clone()
      {
        throw new UnsupportedOperationException("clone is not supported on ConciseSet iterator");
      }

      @Override
      public boolean hasNext()
      {
        return i.hasNext();
      }

      @Override
      public int next()
      {
        return i.next();
      }

    };
  }

  @Override
  public boolean isEmpty()
  {
    return bitmap.size() == 0;
  }

  @Override
  public ImmutableBitmap union(ImmutableBitmap otherBitmap)
  {
    WrappedConciseBitmap other = (WrappedConciseBitmap) otherBitmap;
    ConciseSet unwrappedOtherBitmap = other.bitmap;
    return new WrappedConciseBitmap(bitmap.clone().union(unwrappedOtherBitmap));
  }

  @Override
  public ImmutableBitmap intersection(ImmutableBitmap otherBitmap)
  {
    WrappedConciseBitmap other = (WrappedConciseBitmap) otherBitmap;
    ConciseSet unwrappedOtherBitmap = other.bitmap;
    return new WrappedConciseBitmap(bitmap.clone().intersection(unwrappedOtherBitmap));
  }

  @Override
  public ImmutableBitmap difference(ImmutableBitmap otherBitmap)
  {
    WrappedConciseBitmap other = (WrappedConciseBitmap) otherBitmap;
    ConciseSet unwrappedOtherBitmap = other.bitmap;
    return new WrappedConciseBitmap(bitmap.clone().difference(unwrappedOtherBitmap));
  }

  @Override
  public boolean get(int value)
  {
    return bitmap.contains(value);
  }
}

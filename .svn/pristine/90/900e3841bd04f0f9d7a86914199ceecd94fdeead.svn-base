package com.littlecloud.pool.object;

import java.io.Serializable;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;

public class FirmwareScheduleObject  extends PoolObject implements PoolObjectIf, Serializable {

	/* key */
	public String sn;
	public Integer iana_id;
	
	public static final String sn_default = "FirmwareScheduleObject";
	public static final Integer iana_id_default = 9998;	// dummy
	
	private CopyOnWriteArrayList<Integer> scheduleIdList = new CopyOnWriteArrayList<Integer>();
	
	private int lastupdate;	// clear object if not update for sometimes
		
	@Override
	public String getKey() {
		return this.getClass().getSimpleName() + "sn_pk" + getSn() + "|" + getIana_id();
	}

	@Override
	public void setKey(Integer iana_id, String sn) {
		this.iana_id = iana_id_default;
		this.sn = sn_default;
	}
	
	public String getSn() {
		return sn;
	}

	public void setSn(String sn) {
		this.sn = sn_default;
	}

	public Integer getIana_id() {
		return iana_id;
	}

	public void setIana_id(Integer iana_id) {
		this.iana_id = iana_id_default;
	}

	public CopyOnWriteArrayList<Integer> getScheduleIdList() {
		return scheduleIdList;
	}

	public int getLastupdate() {
		return lastupdate;
	}

	public void setLastupdate(int lastupdate) {
		this.lastupdate = lastupdate;
	}

	/*****/
	
	public void add(int arg0, Integer arg1) {
		scheduleIdList.add(arg0, arg1);
	}

	public boolean add(Integer arg0) {
		return scheduleIdList.add(arg0);
	}

	public boolean addAll(Collection<? extends Integer> arg0) {
		return scheduleIdList.addAll(arg0);
	}

	public boolean addAll(int arg0, Collection<? extends Integer> arg1) {
		return scheduleIdList.addAll(arg0, arg1);
	}

	public int addAllAbsent(Collection<? extends Integer> arg0) {
		return scheduleIdList.addAllAbsent(arg0);
	}

	public boolean addIfAbsent(Integer arg0) {
		return scheduleIdList.addIfAbsent(arg0);
	}

	public void clear() {
		scheduleIdList.clear();
	}

	public Object clone() {
		return scheduleIdList.clone();
	}

	public boolean contains(Object arg0) {
		return scheduleIdList.contains(arg0);
	}

	public boolean containsAll(Collection<?> arg0) {
		return scheduleIdList.containsAll(arg0);
	}

	public boolean equals(Object arg0) {
		return scheduleIdList.equals(arg0);
	}

	public Integer get(int arg0) {
		return scheduleIdList.get(arg0);
	}

	public int hashCode() {
		return scheduleIdList.hashCode();
	}

	public int indexOf(Integer arg0, int arg1) {
		return scheduleIdList.indexOf(arg0, arg1);
	}

	public int indexOf(Object arg0) {
		return scheduleIdList.indexOf(arg0);
	}

	public boolean isEmpty() {
		return scheduleIdList.isEmpty();
	}

	public Iterator<Integer> iterator() {
		return scheduleIdList.iterator();
	}

	public int lastIndexOf(Integer arg0, int arg1) {
		return scheduleIdList.lastIndexOf(arg0, arg1);
	}

	public int lastIndexOf(Object arg0) {
		return scheduleIdList.lastIndexOf(arg0);
	}

	public ListIterator<Integer> listIterator() {
		return scheduleIdList.listIterator();
	}

	public ListIterator<Integer> listIterator(int arg0) {
		return scheduleIdList.listIterator(arg0);
	}

	public Integer remove(int arg0) {
		return scheduleIdList.remove(arg0);
	}

	public boolean remove(Object arg0) {
		return scheduleIdList.remove(arg0);
	}

	public boolean removeAll(Collection<?> arg0) {
		return scheduleIdList.removeAll(arg0);
	}

	public boolean retainAll(Collection<?> arg0) {
		return scheduleIdList.retainAll(arg0);
	}

	public Integer set(int arg0, Integer arg1) {
		return scheduleIdList.set(arg0, arg1);
	}

	public int size() {
		return scheduleIdList.size();
	}

	public List<Integer> subList(int arg0, int arg1) {
		return scheduleIdList.subList(arg0, arg1);
	}

	public Object[] toArray() {
		return scheduleIdList.toArray();
	}

	public <T> T[] toArray(T[] arg0) {
		return scheduleIdList.toArray(arg0);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("FirmwareScheduleObject [sn=");
		builder.append(sn);
		builder.append(", iana_id=");
		builder.append(iana_id);
		builder.append(", scheduleIdList=");
		builder.append(scheduleIdList);
		builder.append(", lastupdate=");
		builder.append(lastupdate);
		builder.append(", createTime=");
		builder.append(createTime);
		builder.append(", Refreshing=");
		builder.append(Refreshing);
		builder.append("]");
		return builder.toString();
	}
	
}

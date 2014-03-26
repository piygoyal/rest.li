/*
   Copyright (c) 2014 LinkedIn Corp.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/

package com.linkedin.restli.test.util;


import com.linkedin.data.schema.PathSpec;
import com.linkedin.data.template.RecordTemplate;
import com.linkedin.restli.client.BatchGetKVRequest;
import com.linkedin.restli.client.Request;
import com.linkedin.restli.client.RequestBuilder;
import com.linkedin.restli.client.response.BatchKVResponse;
import com.linkedin.restli.common.BatchResponse;
import com.linkedin.restli.common.CollectionResponse;
import com.linkedin.restli.common.CreateStatus;
import com.linkedin.restli.common.EmptyRecord;
import com.linkedin.restli.common.OptionsResponse;
import com.linkedin.restli.common.PatchRequest;
import com.linkedin.restli.common.UpdateStatus;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.List;
import java.util.Map;


/**
 * Wrapper of the root request builders for testing purpose.
 *
 * This wrapper allows same test logic to be used on both Rest.li 2 and legacy request builders
 * that are generated for same resource.
 *
 * For the wrapper of method specific builder, check {@link MethodBuilderWrapper}.
 *
 * @author Keren Jin
 */
public class RootBuilderWrapper<K, V extends RecordTemplate>
{
  private final Object _rootBuilder;

  /**
   * Wrapper of the method specific request builders for testing purpose.
   *
   * For the wrapper of method specific builder, check {@link com.linkedin.restli.test.util.RootBuilderWrapper}.
   *
   * @author Keren Jin
   */
  public static class MethodBuilderWrapper<K, V extends RecordTemplate, R>
  {
    private final RequestBuilder<? extends Request<R>> _methodBuilder;

    public MethodBuilderWrapper(RequestBuilder<? extends Request<R>> builder)
    {
      _methodBuilder = builder;
    }

    public Request<R> build()
    {
      try
      {
        @SuppressWarnings("unchecked")
        final Request<R> request = (Request<R>) getMethod("build").invoke(_methodBuilder);
        return request;
      }
      catch (IllegalAccessException e)
      {
        throw new RuntimeException(e);
      }
      catch (InvocationTargetException e)
      {
        throw handleInvocationTargetException(e);
      }
    }

    public BatchGetKVRequest<K, V> buildKV()
    {
      try
      {
        @SuppressWarnings("unchecked")
        final BatchGetKVRequest<K, V> request = (BatchGetKVRequest<K, V>) getMethod("buildKV").invoke(_methodBuilder);
        return request;
      }
      catch (IllegalAccessException e)
      {
        throw new RuntimeException(e);
      }
      catch (InvocationTargetException e)
      {
        throw handleInvocationTargetException(e);
      }
    }

    public RequestBuilder<? extends Request<R>> getBuilder()
    {
      return _methodBuilder;
    }

    public MethodBuilderWrapper<K, V, R> id(K id)
    {
      return invoke(getMethod("id", Object.class), id);
    }

    public MethodBuilderWrapper<K, V, R> ids(Collection<K> ids)
    {
      return invoke(getMethod("ids", Collection.class), ids);
    }

    @SuppressWarnings("unchecked")
    public MethodBuilderWrapper<K, V, R> ids(K... ids)
    {
      return invoke(getMethod("ids", Object[].class), (Object) ids);
    }

    public MethodBuilderWrapper<K, V, R> input(V entity)
    {
      return invoke(getMethod("input", RecordTemplate.class), entity);
    }

    public MethodBuilderWrapper<K, V, R> input(PatchRequest<V> entity)
    {
      return invoke(getMethod("input", PatchRequest.class), entity);
    }

    public MethodBuilderWrapper<K, V, R> input(K id, V entity)
    {
      return invoke(getMethod("input", Object.class, RecordTemplate.class), id, entity);
    }

    public MethodBuilderWrapper<K, V, R> input(K id, PatchRequest<V> patch)
    {
      return invoke(getMethod("input", Object.class, PatchRequest.class), id, patch);
    }

    public MethodBuilderWrapper<K, V, R> inputs(List<V> entities)
    {
      return invoke(getMethod("inputs", List.class), entities);
    }

    public MethodBuilderWrapper<K, V, R> inputs(Map<K, V> entities)
    {
      return invoke(getMethod("inputs", Map.class), entities);
    }

    public MethodBuilderWrapper<K, V, R> patchInputs(Map<K, PatchRequest<V>> entities)
    {
      return invoke(getMethod("inputs", Map.class), entities);
    }

    public MethodBuilderWrapper<K, V, R> fields(PathSpec... fieldPaths)
    {
      return invoke(getMethod("fields", PathSpec[].class), (Object) fieldPaths);
    }

    public MethodBuilderWrapper<K, V, R> name(String name)
    {
      return invoke(getMethod("name", String.class), name);
    }

    public MethodBuilderWrapper<K, V, R> assocKey(String key, Object value)
    {
      return invoke(getMethod("assocKey", String.class, Object.class), key, value);
    }

    public MethodBuilderWrapper<K, V, R> paginate(int start, int count)
    {
      return invoke(getMethod("paginate", int.class, int.class), start, count);
    }

    public MethodBuilderWrapper<K, V, R> paginateStart(int start)
    {
      return invoke(getMethod("paginateStart", int.class), start);
    }

    public MethodBuilderWrapper<K, V, R> paginateCount(int count)
    {
      return invoke(getMethod("paginateCount", int.class), count);
    }

    public MethodBuilderWrapper<K, V, R> setHeader(String name, String value)
    {
      return invoke(getMethod("setHeader", String.class, String.class), name, value);
    }

    public MethodBuilderWrapper<K, V, R> setParam(String name, Object value)
    {
      return invoke(getMethod("setParam", String.class, Object.class), name, value);
    }

    public MethodBuilderWrapper<K, V, R> setQueryParam(String name, Object value)
    {
      final String methodName = name + "Param";
      final Method method;
      if (value instanceof Iterable)
      {
        method = getMethod(methodName, Iterable.class);
      }
      else
      {
        method = findMethod(methodName, value);
      }

      return invoke(method, value);
    }

    public MethodBuilderWrapper<K, V, R> addQueryParam(String name, Object value)
    {
      return invoke(findMethod("add" + name + "Param", value), value);
    }

    public MethodBuilderWrapper<K, V, R> setActionParam(String name, Object value)
    {
      return invoke(findMethod("param" + name, value), value);
    }

    public MethodBuilderWrapper<K, V, R> setPathKey(String name, Object value)
    {
      return invoke(findMethod(name + "Key", value), value);
    }

    private MethodBuilderWrapper<K, V, R> invoke(Method method, Object... args)
    {
      try
      {
        @SuppressWarnings("unchecked")
        final RequestBuilder<? extends Request<R>> builder = (RequestBuilder<? extends Request<R>>) method.invoke(_methodBuilder, args);
        return new MethodBuilderWrapper<K, V, R>(builder);
      }
      catch (IllegalAccessException e)
      {
        throw new RuntimeException(e);
      }
      catch (InvocationTargetException e)
      {
        throw handleInvocationTargetException(e);
      }
    }

    private Method getMethod(String name, Class<?>... parameterTypes)
    {
      try
      {
        return _methodBuilder.getClass().getMethod(name, parameterTypes);
      }
      catch (NoSuchMethodException e)
      {
        throw new RuntimeException(e);
      }
    }

    private Method findMethod(String name, Object value)
    {
      if (value == null)
      {
        for (Method m : _methodBuilder.getClass().getMethods())
        {
          if (m.getName().equals(name) && m.getParameterTypes().length == 1)
          {
            return m;
          }
        }

        return null;
      }
      else
      {
        return getMethod(name, value.getClass());
      }
    }
  }

  public RootBuilderWrapper(Object builder)
  {
    _rootBuilder = builder;
  }

  public Object getBuilder()
  {
    return _rootBuilder;
  }

  public MethodBuilderWrapper<K, V, V> get()
  {
    return invoke("get");
  }

  public MethodBuilderWrapper<K, V, EmptyRecord> create()
  {
    return invoke("create");
  }

  public MethodBuilderWrapper<K, V, EmptyRecord> update()
  {
    return invoke("update");
  }

  public MethodBuilderWrapper<K, V, EmptyRecord> delete()
  {
    return invoke("delete");
  }

  public MethodBuilderWrapper<K, V, EmptyRecord> partialUpdate()
  {
    return invoke("partialUpdate");
  }

  public MethodBuilderWrapper<K, V, BatchResponse<V>> batchGet()
  {
    return invoke("batchGet");
  }

  public MethodBuilderWrapper<K, V, CollectionResponse<CreateStatus>> batchCreate()
  {
    return invoke("batchCreate");
  }

  public MethodBuilderWrapper<K, V, BatchKVResponse<K, UpdateStatus>> batchPartialUpdate()
  {
    return invoke("batchPartialUpdate");
  }

  public MethodBuilderWrapper<K, V, BatchKVResponse<K, UpdateStatus>> batchUpdate()
  {
    return invoke("batchUpdate");
  }

  public MethodBuilderWrapper<K, V, BatchKVResponse<K, UpdateStatus>> batchDelete()
  {
    return invoke("batchDelete");
  }

  public MethodBuilderWrapper<K, V, CollectionResponse<V>> getAll()
  {
    return invoke("getAll");
  }

  public MethodBuilderWrapper<K, V, OptionsResponse> options()
  {
    return invoke("options");
  }

  public MethodBuilderWrapper<K, V, CollectionResponse<V>> findBy(String name)
  {
    return invoke("findBy" + name);
  }

  public <R> MethodBuilderWrapper<K, V, R> action(String name)
  {
    return invoke("action" + name);
  }

  private <R> MethodBuilderWrapper<K, V, R> invoke(String methodName)
  {
    try
    {
      @SuppressWarnings("unchecked")
      final RequestBuilder<? extends Request<R>> builder = (RequestBuilder<? extends Request<R>>) _rootBuilder.getClass().getMethod(methodName).invoke(_rootBuilder);
      return new MethodBuilderWrapper<K, V, R>(builder);
    }
    catch (NoSuchMethodException e)
    {
      throw new RuntimeException(e);
    }
    catch (IllegalAccessException e)
    {
      throw new RuntimeException(e);
    }
    catch (InvocationTargetException e)
    {
      throw handleInvocationTargetException(e);
    }
  }

  private static RuntimeException handleInvocationTargetException(InvocationTargetException e)
  {
    final Throwable targetException = e.getTargetException();
    if (targetException instanceof RuntimeException)
    {
      return (RuntimeException) targetException;
    }
    else
    {
      return new RuntimeException(e.getTargetException());
    }
  }
}